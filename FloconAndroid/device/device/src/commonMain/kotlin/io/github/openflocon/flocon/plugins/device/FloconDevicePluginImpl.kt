package io.github.openflocon.flocon.plugins.device

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.error.pluginNotInitialized

class FloconDeviceConfig : FloconPluginConfig

/**
 * Device actions that need an extra dependency, so they are not part of the core: restarting the app.
 * The app icon and the adb serial are sent by the core, without installing this plugin.
 */
interface FloconDevicePlugin : FloconPlugin

object FloconDevice : FloconPluginFactory<FloconDeviceConfig, FloconDevicePlugin> {
    override val name: String = "Device"
    override val pluginId: String = Protocol.ToDevice.Device.Plugin
    override fun createConfig(context: FloconContext) = FloconDeviceConfig()
    override fun install(
        pluginConfig: FloconDeviceConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconDevicePlugin = FloconDevicePluginImpl(context = floconConfig.context)
        .also { FloconDevicePluginImpl.plugin = it }
}

internal expect fun restartApp(context: FloconContext)

internal class FloconDevicePluginImpl(
    private val context: FloconContext,
) : FloconPlugin, FloconDevicePlugin {
    override val key: String = Protocol.ToDevice.Device.Plugin

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        when (method) {
            Protocol.ToDevice.Device.Method.RestartApp -> restartApp(context)
        }
    }

    override suspend fun onConnectedToServer() {
        // no op
    }

    companion object {
        var plugin: FloconDevicePlugin? = null
    }
}

val Flocon.Companion.devicePlugin: FloconDevicePlugin
    get() = FloconDevicePluginImpl.plugin ?: pluginNotInitialized("Device", NoOpDevicePlugin())

private class NoOpDevicePlugin : FloconDevicePlugin {
    override val key: String = ""
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}
