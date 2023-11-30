package com.pythonbyte.mightyant.config

data class MightyAntConfig(
    var proxyPort: Int = 9000,
    var destinationUrl: String = "ws://localhost:8080/",
    var proxyName: String = "MightyAnt",
    var silent: Boolean = false,
)
