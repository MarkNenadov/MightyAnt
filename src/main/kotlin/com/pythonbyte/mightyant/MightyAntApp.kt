package com.pythonbyte.mightyant

import com.pythonbyte.mightyant.config.MightyAntConfig
import getLogger
import org.pythonbyte.krux.files.resourceToInputStream
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException
import java.io.IOException

private val logger = getLogger(MightyAntConfig::class.java)

/**
 * Main entry point for the MightyAnt application.
 * Initializes the configuration and starts the WebSocket proxy server.
 */
fun main() {
    val config = getConfig()

    logger.info("Loading MightyAnt...")
    WebSocketProxyRunner(config).run()

    with(config) {
        logger.info(
            "Listening for Proxying on port $proxyPort in ${if (arbitraryMode) "Arbitrary Mode" else "Regular Mode"}",
        )
    }
}

/**
 * Loads and validates the application configuration from the YAML file.
 *
 * @return A non-null [MightyAntConfig] instance
 * @throws IllegalStateException if the configuration cannot be loaded or is invalid
 */
fun getConfig(): MightyAntConfig {
    return try {
        val config =
            Yaml().loadAs(
                resourceToInputStream("config.yaml"),
                MightyAntConfig::class.java,
            )

        if (config == null) {
            throw IllegalStateException("Configuration file is empty")
        }

        config
    } catch (e: IOException) {
        throw IllegalStateException("Failed to read configuration file: ${e.message}", e)
    } catch (e: YAMLException) {
        throw IllegalStateException("Invalid YAML configuration format: ${e.message}", e)
    } catch (e: Exception) {
        throw IllegalStateException("Unexpected error loading configuration: ${e.message}", e)
    }
}
