package com.pythonbyte.mightyant

import com.pythonbyte.mightyant.config.MightyAntConfig
import getLogger
import org.pythonbyte.krux.files.resourceToInputStream
import org.yaml.snakeyaml.Yaml

private val logger = getLogger(MightyAntConfig::class.java)

fun main() {
    val config = getConfig()
    val proxyListener = WebSocketProxyRunner(config)

    logger.info("Loading MightyAnt...")
    proxyListener.run()
    logger.info(
        "Listening for Proxying on port ${config.proxyPort} in ${if (config.arbitraryMode) "Arbitrary Mode" else "Regular Mode"}"
    )
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
