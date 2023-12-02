package com.pythonbyte.mightyant

import com.pythonbyte.mightyant.config.MightyAntConfig
import com.pythonbyte.mightyant.util.sendToWebsocket
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsMessage
import org.pythonbyte.krux.json.JsonObject
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

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
                if (config.arbitraryMode) {
                    val bytes = ByteArray(it.body.payload.remaining())
                    it.body.payload.get(bytes)
                    val jsonObject = JsonObject(String(bytes, Charsets.UTF_8))

                    sendToWebsocket(
                        jsonObject.getString("url"),
                        jsonObject.getString("content"),
                        proxySocket,
                    )
                } else {
                    sendToWebsocket(config.destinationUrl, it.body, proxySocket)

                    config.mirrorUrls.forEach { mirrorUrl: String ->
                        sendToWebsocket(mirrorUrl, it.body, proxySocket)
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

fun getConfig(): MightyAntConfig? {
    val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream("config.yaml")
    if (inputStream != null) {
        return Yaml(Constructor(MightyAntConfig::class.java)).load(inputStream) as MightyAntConfig
    }
    return null
}
