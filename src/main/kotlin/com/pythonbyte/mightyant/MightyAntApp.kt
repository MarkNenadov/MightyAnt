package com.pythonbyte.mightyant

import com.pythonbyte.mightyant.config.MightyAntConfig
import com.pythonbyte.mightyant.util.sendToWebsocket
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsMessage
import org.pythonbyte.krux.json.JsonObject
import org.pythonbyte.krux.string.asString
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.zip.GZIPOutputStream
import kotlin.text.Charsets.UTF_8

fun main() {
    val config = getConfig()

    if (config != null) {
        { proxySocket: Websocket ->
            if (!config.silent) {
                proxySocket.send(
                    WsMessage("Welcome to ${config.proxyName} Proxy"),
                )
            }
            proxySocket.onMessage {
                val content = it.body.payload.asString()

                if (config.arbitraryMode) {
                    val jsonObject = JsonObject(content)

                    sendToWebsocket(
                        jsonObject.getString("url"),
                        transformations(config, jsonObject.getString("content")),
                        proxySocket,
                    )
                } else {
                    sendToWebsocket(
                        config.destinationUrl,
                        transformations(config, content),
                        proxySocket,
                    )

                    config.mirrorUrls.forEach { mirrorUrl: String ->
                        sendToWebsocket(mirrorUrl, content, proxySocket)
                    }
                }
            }
        }.asServer(
            Jetty(config.proxyPort),
        ).start()
    } else {
        println("Unable to start Proxy, due to missing resources/config.yaml")
    }
}

fun transformations(config: MightyAntConfig, content: String): String {
    var newContent = content

    if (config.useBase64) {
        val byteArray = newContent.toByteArray()
        newContent = Base64.getEncoder().encodeToString(byteArray)
    }

    if (config.useCompression) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        GZIPOutputStream(byteArrayOutputStream).bufferedWriter(UTF_8).use { it.write(content) }
        newContent = String(byteArrayOutputStream.toByteArray())
    }

    return newContent
}

fun getConfig(): MightyAntConfig? {
    val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream("config.yaml")
    if (inputStream != null) {
        return Yaml(Constructor(MightyAntConfig::class.java)).load(inputStream) as MightyAntConfig
    }
    return null
}
