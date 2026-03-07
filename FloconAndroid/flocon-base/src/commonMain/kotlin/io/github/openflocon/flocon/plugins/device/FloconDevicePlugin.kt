package io.github.openflocon.flocon.plugins.device

import io.github.openflocon.flocon.*

class FloconDeviceConfig

/**
 * Flocon Device Plugin.
 */
expect object FloconDevice : FloconPluginFactory<FloconDeviceConfig, FloconDevicePlugin>

interface FloconDevicePlugin : FloconPlugin {
    fun registerWithSerial(serial: String)
}