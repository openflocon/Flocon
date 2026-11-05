package io.github.openflocon.flocon.device

import io.github.openflocon.flocon.FloconContext

internal actual fun getAppIconBase64(context: FloconContext): String? {
    return null
}
internal actual fun observeDeviceSerial(onSerial: (serial: String) -> Unit) {
    // no op: the serial is only sent through an adb broadcast on Android
}
