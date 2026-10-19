package io.github.openflocon.flocon.plugins.device

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.encode
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.error.pluginNotInitialized
import io.github.openflocon.flocon.plugins.device.model.fromdevice.RegisterDeviceDataModel

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
        val plugin = FloconDevicePluginImpl(
            sender = floconConfig.client as FloconMessageSender,
            context = floconConfig.context,
            encoder = encoder,
        )
            .also { FloconDevicePluginImpl.plugin = it }
        observeDeviceSerial(onSerial = plugin::registerWithSerial)
        return plugin
    }
}

internal expect fun restartApp(context: FloconContext)

/**
 * Calls [onSerial] when the desktop sends the device serial (adb broadcast on Android)
 */
internal expect fun observeDeviceSerial(onSerial: (serial: String) -> Unit)

internal class FloconDevicePluginImpl(
    private var sender: FloconMessageSender,
    private val context: FloconContext,
    private val encoder: FloconEncoder,
) : FloconPlugin, FloconDevicePlugin {
    override val key: String = Protocol.ToDevice.Device.Plugin

    override fun registerWithSerial(serial: String) {
        try {
            sender.send(
                plugin = Protocol.FromDevice.Device.Plugin,
                method = Protocol.FromDevice.Device.Method.RegisterDevice,
                body = encoder.encode(RegisterDeviceDataModel(serial)),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("Device parsing error", t)
        }
    }

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        when (method) {
            Protocol.ToDevice.Device.Method.GetAppIcon -> {
                val icon = getAppIconBase64(context)
                if (icon != null) {
                    sender.send(
                        plugin = Protocol.FromDevice.Device.Plugin,
                        method = Protocol.FromDevice.Device.Method.AppIcon,
                        body = icon,
                    )
                }
            }

            Protocol.ToDevice.Device.Method.RestartApp -> {
                restartApp(context)
            }
        }
    }

    override suspend fun onConnectedToServer() {
        // no op
    }

    companion object {
        var plugin: FloconDevicePlugin? = null
    }
}

@OptIn(FloconMarker::class)
val Flocon.Companion.devicePlugin: FloconDevicePlugin
    get() = FloconDevicePluginImpl.plugin ?: pluginNotInitialized("Device", NoOpDevicePlugin())

private class NoOpDevicePlugin : FloconDevicePlugin {
    override val key: String = ""
    override fun registerWithSerial(serial: String) = Unit
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}
