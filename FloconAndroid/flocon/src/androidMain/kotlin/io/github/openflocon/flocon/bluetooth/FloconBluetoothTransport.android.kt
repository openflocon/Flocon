package io.github.openflocon.flocon.bluetooth

import io.github.openflocon.flocon.FloconContext

internal actual fun buildFloconBluetoothTransport(context: FloconContext): FloconBluetoothTransport =
    FloconBluetoothTransportAndroid(context = context.context)
