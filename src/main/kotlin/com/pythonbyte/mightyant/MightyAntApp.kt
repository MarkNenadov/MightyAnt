package com.pythonbyte.mightyant

import com.pythonbyte.mightyant.config.MightyAntConfig
import org.yaml.snakeyaml.Yaml
import java.io.InputStream

fun main() {
    val proxyListener = WebSocketProxyRunner(getConfig())
    proxyListener.run()
}


fun getConfig(): MightyAntConfig {
    val inputStream: InputStream = Thread.currentThread().contextClassLoader.getResourceAsStream("config.yaml")
        ?: throw Exception("Can't load Mighty Ant config file (resources/config.yaml)")

    val yaml = Yaml()

    try {
        return yaml.loadAs(inputStream, MightyAntConfig::class.java) ?: MightyAntConfig()
    } catch (e: Exception) {
        throw Exception("Error loading YAML configuration", e)
    }
}