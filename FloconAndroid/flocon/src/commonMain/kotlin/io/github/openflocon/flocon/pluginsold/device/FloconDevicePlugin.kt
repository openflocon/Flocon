package io.github.openflocon.flocon.pluginsold.device

import io.github.openflocon.flocon.*

class FloconDeviceConfig : FloconPluginConfig

/**
 * Flocon Device Plugin.
 */
object FloconDevice : FloconPluginFactory<FloconDeviceConfig, FloconDevicePlugin> {
    override fun createConfig(): FloconDeviceConfig {
        TODO("Not yet implemented")
    }

    override fun install(config: FloconDeviceConfig, app: FloconApp): FloconDevicePlugin {
        TODO("Not yet implemented")
    }

    override val name: String
        get() = TODO("Not yet implemented")
}

interface FloconDevicePlugin : FloconPlugin {
    fun registerWithSerial(serial: String)
}