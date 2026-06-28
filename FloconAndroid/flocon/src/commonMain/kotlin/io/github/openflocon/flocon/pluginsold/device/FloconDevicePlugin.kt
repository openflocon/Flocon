package io.github.openflocon.flocon.pluginsold.device

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.core.FloconEncoder

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
        floconConfig: FloconConfig,
        encoder: FloconEncoder
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