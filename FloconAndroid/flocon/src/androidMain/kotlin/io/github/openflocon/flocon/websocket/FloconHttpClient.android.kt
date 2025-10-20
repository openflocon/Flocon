package io.github.openflocon.flocon.websocket

internal actual fun buildFloconHttpClient(): FloconHttpClient {
    return FloconHttpClientImpl()
}