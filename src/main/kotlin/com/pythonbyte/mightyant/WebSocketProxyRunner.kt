package com.pythonbyte.mightyant

import com.pythonbyte.mightyant.config.MightyAntConfig
import com.pythonbyte.mightyant.util.sendToWebsocket
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsMessage
import org.pythonbyte.krux.json.JsonObject
import org.pythonbyte.krux.string.asString
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.GZIPOutputStream

class WebSocketProxyRunner(val config: MightyAntConfig): Runnable {
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

    private fun transformations(content: String): String {
        var newContent = content

        if (config.useBase64) {
            val byteArray = newContent.toByteArray()
            newContent = Base64.getEncoder().encodeToString(byteArray)
        }

        if (config.useCompression) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            GZIPOutputStream(byteArrayOutputStream).bufferedWriter(Charsets.UTF_8).use { it.write(content) }
            newContent = String(byteArrayOutputStream.toByteArray())
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
