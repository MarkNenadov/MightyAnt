package com.pythonbyte.mightyant

import com.pythonbyte.mightyant.config.MightyAntConfig
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

fun main() {
    val proxyListener = WebSocketProxyRunner(getConfig())
    proxyListener.run()
}

fun getConfig(): MightyAntConfig {
    val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream("config.yaml")
    if (inputStream != null) {
        return Yaml(Constructor(MightyAntConfig::class.java)).load(inputStream) as MightyAntConfig
    }
    throw Exception("Can't load Mighty Ant config file (resources/config.yaml)")
}
