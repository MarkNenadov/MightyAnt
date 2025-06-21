package com.pythonbyte.mightyant.util

import org.http4k.client.WebsocketClient
import org.http4k.core.Uri
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsMessage
import org.java_websocket.exceptions.WebsocketNotConnectedException

fun sendToWebsocket(
    url: String,
    bodyContent: String,
    proxySocket: Websocket,
) {
    try {
        val proxiedClient =
            WebsocketClient.blocking(
                Uri.of(url),
            )

        proxiedClient.run {
            this.send(
                WsMessage(bodyContent),
            )
        }
    } catch (e: WebsocketNotConnectedException) {
        proxySocket.send(WsMessage("Proxying failed [$e]"))
    }
}
