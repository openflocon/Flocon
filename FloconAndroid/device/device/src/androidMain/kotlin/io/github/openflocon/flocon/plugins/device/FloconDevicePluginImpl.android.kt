package io.github.openflocon.flocon.plugins.device

import com.jakewharton.processphoenix.ProcessPhoenix
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconDeviceSerial

internal actual fun restartApp(context: FloconContext) {
    ProcessPhoenix.triggerRebirth(context.context)
}

internal actual fun observeDeviceSerial(onSerial: (serial: String) -> Unit) {
    FloconDeviceSerial.observe(onSerial)
}
