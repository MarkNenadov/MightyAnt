package com.pythonbyte.mightyant.util

import org.http4k.client.WebsocketClient
import org.http4k.core.Body
import org.http4k.core.Uri
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsMessage

fun sendToWebSocket(url: String, body: Body, parentSocket: Websocket?) {
    val client = WebsocketClient.nonBlocking(
        Uri.of(url),
    )

    client.run {
        this.send(WsMessage(body))
    }
}
