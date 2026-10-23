package io.github.openflocon.flocon.bluetooth

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.websocket.FloconHttpClient
import io.github.openflocon.flocon.websocket.FloconWebSocketClient

/**
 * Replaces the websocket and the http clients when the app talks to the desktop over Bluetooth:
 * one RFCOMM link carries the messages and the files. Android only.
 */
internal interface FloconBluetoothTransport : FloconWebSocketClient, FloconHttpClient

internal expect fun buildFloconBluetoothTransport(context: FloconContext): FloconBluetoothTransport
