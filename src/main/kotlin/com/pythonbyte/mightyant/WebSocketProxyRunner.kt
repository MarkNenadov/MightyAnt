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

        sendToWebsocket(
            config.destinationUrl,
            transformations(content),
            proxySocket,
        )

        config.mirrorUrls.forEach { mirrorUrl: String ->
            logger.debug("Regular mode proxy mirror to => ${config.destinationUrl}")
            sendToWebsocket(mirrorUrl, content, proxySocket)
        }
    }

    private fun processArbitraryMode(content: String) {
        logPayloadReceived(content)

        with(JsonObject(content)) {
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
        var newContent = content

        if (config.useBase64) {
            logger.debug("Base64 transformation applied")
            newContent = newContent.base64()
        }

        if (config.useCompression) {
            logger.debug("Gzip compression transformation applied")
            newContent = newContent.gzip()
        }

        return newContent
    }

    override fun run() {
        {
                proxySocket: Websocket ->
            this.proxySocket = proxySocket
            if (!config.silent) {
                sendWelcomeMessage(proxySocket)
            }
            proxySocket.onMessage {
                with(it.body.payload.asString()) {
                    if (config.arbitraryMode) {
                        processArbitraryMode(this)
                    } else {
                        logPayloadReceived(this)
                        processRegularMode(this)
                    }
                }
            }
        }.asServer(
            Jetty(config.proxyPort),
        ).start()
    }

    companion object {
        private val logger = getLogger(this::class.java)
        private const val PAYLOAD_LOGGING_CHAR_LIMIT = 200
    }
}
