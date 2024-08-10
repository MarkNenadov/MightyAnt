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

    private fun processArbitraryMode(content: String) {
        JsonObject(content).apply {
            val payload = getString("content")

            sendToWebsocket(
                getString("url"),
                transformations(payload),
                proxySocket,
            )
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
                message.process()
            }
        }.asServer(
            Jetty(config.proxyPort),
        ).start()
    }

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
