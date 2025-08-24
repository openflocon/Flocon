package io.github.openflocon.flocon.plugins.device

import io.github.openflocon.flocon.core.FloconPlugin

interface FloconDevicePlugin : FloconPlugin {
    fun registerWithSerial(serial: String)
}