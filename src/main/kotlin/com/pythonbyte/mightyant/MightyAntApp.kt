package com.pythonbyte.mightyant

import com.pythonbyte.mightyant.config.MightyAntConfig
import getLogger
import org.pythonbyte.krux.files.resourceToInputStream
import org.yaml.snakeyaml.Yaml

private val logger = getLogger(MightyAntConfig::class.java)

fun main() {
    val config = getConfig()

    logger.info("Loading MightyAnt...")
    WebSocketProxyRunner(config).run()

    with(config) {
        logger.info(
            "Listening for Proxying on port $proxyPort in ${if (arbitraryMode) "Arbitrary Mode" else "Regular Mode"}"
        )
    }
}

fun getConfig(): MightyAntConfig {
    try {
        return Yaml().loadAs(
            resourceToInputStream("config.yaml"),
            MightyAntConfig::class.java,
        ) ?: MightyAntConfig()
    } catch (e: Exception) {
        throw Exception("Error loading YAML configuration", e)
    }
}
