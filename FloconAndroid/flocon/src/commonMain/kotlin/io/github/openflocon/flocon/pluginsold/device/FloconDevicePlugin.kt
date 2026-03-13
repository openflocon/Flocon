package io.github.openflocon.flocon.pluginsold.device

import io.github.openflocon.flocon.*

class FloconDeviceConfig : FloconPluginConfig

/**
 * Flocon Device Plugin.
 */
object FloconDevice : FloconPluginFactory<FloconDeviceConfig, FloconDevicePlugin> {
    override fun createConfig(context: FloconContext): FloconDeviceConfig {
        TODO("Not yet implemented")
    }

    override fun install(
        pluginConfig: FloconDeviceConfig,
        floconConfig: FloconConfig
    ): FloconDevicePlugin {
        TODO("Not yet implemented")
    }

    override val name: String
        get() = TODO("Not yet implemented")
    override val pluginId: String
        get() = TODO("Not yet implemented")
}

interface FloconDevicePlugin : FloconPlugin {
    fun registerWithSerial(serial: String)
}