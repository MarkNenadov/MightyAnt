package com.pythonbyte.mightyant

import com.pythonbyte.mightyant.config.MightyAntConfig
import com.pythonbyte.mightyant.util.sendToWebsocket
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsMessage
import org.pythonbyte.krux.conversions.asString
import org.pythonbyte.krux.conversions.base64
import org.pythonbyte.krux.conversions.gzip
import org.pythonbyte.krux.json.JsonObject
import java.util.*
import kotlin.io.encoding.ExperimentalEncodingApi

class WebSocketProxyRunner(val config: MightyAntConfig) : Runnable {
    private lateinit var proxySocket: Websocket

    private fun processRegularMode(content: String) {
        sendToWebsocket(
            config.destinationUrl,
            transformations(content),
            proxySocket,
        )

        config.mirrorUrls.forEach { mirrorUrl: String ->
            sendToWebsocket(mirrorUrl, content, proxySocket)
        }
    }

    private fun processArbitraryMode(content: String) {
        val jsonObject = JsonObject(content)

        sendToWebsocket(
            jsonObject.getString("url"),
            transformations(jsonObject.getString("content")),
            proxySocket,
        )
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
            newContent = newContent.base64()
        }

        if (config.useCompression) {
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
                val content = it.body.payload.asString()

                if (config.arbitraryMode) {
                    processArbitraryMode(content)
                } else {
                    processRegularMode(content)
                }
            }
        }.asServer(
            Jetty(config.proxyPort),
        ).start()
    }
}
