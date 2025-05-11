package com.pythonbyte.mightyant

import com.pythonbyte.mightyant.config.MightyAntConfig
import com.pythonbyte.mightyant.util.sendToWebsocket
import getLogger
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsMessage
import org.pythonbyte.krux.conversions.asString
import org.pythonbyte.krux.conversions.base64
import org.pythonbyte.krux.conversions.gzip
import org.pythonbyte.krux.json.JsonObject
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Handles WebSocket proxy operations for the MightyAnt application.
 * Supports both regular and arbitrary modes of operation.
 *
 * @property config The application configuration
 */
class WebSocketProxyRunner(private val config: MightyAntConfig) : Runnable {
    private lateinit var proxySocket: Websocket

    private fun processRegularMode(content: String) {
        logger.debug("Regular mode proxy to => ${config.destinationUrl}")

        val transformedContent = transformations(content)
        sendToWebsocket(
            config.destinationUrl,
            transformedContent,
            proxySocket,
        )

        config.mirrorUrls.forEach { mirrorUrl: String ->
            logger.debug("Regular mode proxy mirror to => ${config.destinationUrl}")
            sendToWebsocket(mirrorUrl, transformedContent, proxySocket)
        }
    }

    /**
     * Processes messages in arbitrary mode, extracting destination URL and content from the message.
     */
    private fun processArbitraryMode(content: String) {
        try {
            JsonObject(content).apply {
                val payload = getString("content")
                val url = getString("url")

                sendToWebsocket(
                    url,
                    transformations(payload),
                    proxySocket,
                )
            }
        } catch (e: Exception) {
            logger.error("Failed to process arbitrary mode message: ${e.message}")
            proxySocket.send(WsMessage("Error processing message: ${e.message}"))
        }
    }

    private fun logPayloadReceived(content: String) {
        logger.debug("Payload received [${content.take(PAYLOAD_LOGGING_CHAR_LIMIT)}]")
    }

    private fun sendWelcomeMessage(proxySocket: Websocket) {
        proxySocket.send(
            WsMessage("Welcome to ${config.proxyName} Proxy"),
        )
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun transformations(content: String): String {
        return content
            .takeIf { config.useBase64 }?.base64()
            .takeIf { config.useCompression }?.gzip()
            ?: content
    }

    override fun run() {
        { proxySocket: Websocket ->
            this.proxySocket = proxySocket
            if (!config.silent) {
                sendWelcomeMessage(proxySocket)
            }
            proxySocket.onMessage { message ->
                try {
                    message.process()
                } catch (e: Exception) {
                    logger.error("Error processing WebSocket message: ${e.message}")
                    proxySocket.send(WsMessage("Error processing message: ${e.message}"))
                }
            }
        }.asServer(
            Jetty(config.proxyPort),
        ).start()
    }

    /**
     * Processes an incoming WebSocket message.
     */
    private fun WsMessage.process() {
        body.payload.asString().apply {
            logPayloadReceived(this)

            if (config.arbitraryMode) {
                processArbitraryMode(this)
            } else {
                processRegularMode(this)
            }
        }
    }

    companion object {
        private val logger = getLogger(this::class.java)
        private const val PAYLOAD_LOGGING_CHAR_LIMIT = 200
    }
}
