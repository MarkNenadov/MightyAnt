package com.pythonbyte.mightyant

import com.pythonbyte.mightyant.config.MightyAntConfig
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsMessage
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

fun main() {
    val proxyListener = WebSocketProxyRunner(getConfig())
    proxyListener.run()
}

fun sendWelcomeMessage(proxySocket: Websocket, config: MightyAntConfig) {
    proxySocket.send(
        WsMessage("Welcome to ${config.proxyName} Proxy"),
    )
}

fun getConfig(): MightyAntConfig {
    val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream("config.yaml")
    if (inputStream != null) {
        return Yaml(Constructor(MightyAntConfig::class.java)).load(inputStream) as MightyAntConfig
    }
    throw Exception("Can't load Mighty Ant config file (resources/config.yaml)")
}
