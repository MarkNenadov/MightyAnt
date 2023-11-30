package com.pythonbyte.mightyant

import com.pythonbyte.mightyant.config.MightyAntConfig
import org.http4k.client.WebsocketClient
import org.http4k.core.Uri
import org.http4k.websocket.WsMessage

fun proxyRequest(it: WsMessage, config: MightyAntConfig) {
    val client = WebsocketClient.blocking(Uri.of(config.destinationUrl))
    client.send(WsMessage(it.body))
    client.close()
}
