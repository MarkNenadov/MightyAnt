package com.pythonbyte.mightyant.config

data class MightyAntConfig(
    var proxyPort: Int = 9000,
    var destinationUrl: String = "ws://localhost:8080/",
    var mirrorUrls: List<String> = mutableListOf(),
    var proxyName: String = "MightyAnt",
    var silent: Boolean = false,
    var arbitraryMode: Boolean = false,
    var useCompression: Boolean = false,
    var useBase64: Boolean = false,
)
