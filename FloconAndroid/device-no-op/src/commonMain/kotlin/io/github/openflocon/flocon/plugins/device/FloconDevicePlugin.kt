package io.github.openflocon.flocon.plugins.device

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder

class FloconDeviceConfig : FloconPluginConfig

interface FloconDevicePlugin : FloconPlugin {
    fun registerWithSerial(serial: String)
}

object FloconDevice : FloconPluginFactory<FloconDeviceConfig, FloconDevicePlugin> {
    override val name: String = "Device"
    override val pluginId: String = Protocol.ToDevice.Device.Plugin
    override fun createConfig(context: FloconContext) = FloconDeviceConfig()
    override fun install(
        pluginConfig: FloconDeviceConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconDevicePlugin {
        return FloconDevicePluginNoOpImpl()
    }
}

internal class FloconDevicePluginNoOpImpl : FloconPlugin, FloconDevicePlugin {
    override val key: String = "DEVICE"

    override fun registerWithSerial(serial: String) {
        // no op
    }

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        // no op
    }

    override suspend fun onConnectedToServer() {
        // no op
    }
}
