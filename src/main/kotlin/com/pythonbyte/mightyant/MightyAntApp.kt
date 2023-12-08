package com.pythonbyte.mightyant

import com.pythonbyte.mightyant.config.MightyAntConfig
import org.pythonbyte.krux.files.resourceToInputStream
import org.yaml.snakeyaml.Yaml

fun main() {
    val proxyListener = WebSocketProxyRunner(getConfig())
    proxyListener.run()
}

fun getConfig(): MightyAntConfig {
    val yaml = Yaml()

    try {
        return yaml.loadAs(
            resourceToInputStream("config.yaml"),
            MightyAntConfig::class.java,
        ) ?: MightyAntConfig()
    } catch (e: Exception) {
        throw Exception("Error loading YAML configuration", e)
    }
}
