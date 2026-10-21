package io.github.openflocon.flocon.plugins.device

import io.github.openflocon.flocon.FloconContext

internal actual fun restartApp(context: FloconContext) {
}

internal actual fun observeDeviceSerial(onSerial: (serial: String) -> Unit) {
    // no op : the serial is only sent through an adb broadcast on Android
}
