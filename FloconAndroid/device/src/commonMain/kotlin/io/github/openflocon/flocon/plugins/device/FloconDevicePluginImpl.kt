package io.github.openflocon.flocon.plugins.device

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender

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
        return FloconDevicePluginImpl(
            sender = floconConfig.client as FloconMessageSender,
            context = floconConfig.context
        )
    }
}

internal expect fun restartApp(context: FloconContext)

internal class FloconDevicePluginImpl(
    private var sender: FloconMessageSender,
    private val context: FloconContext,
) : FloconPlugin, FloconDevicePlugin {
    override val key: String = "DEVICE"

    override fun registerWithSerial(serial: String) {
        try {
//            sender.send(
//                plugin = Protocol.FromDevice.Device.Plugin,
//                method = Protocol.FromDevice.Device.Method.RegisterDevice,
//                body = RegisterDeviceDataModel(serial).toJson().toString(),
//            )
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
//                    sender.send(
//                        plugin = Protocol.FromDevice.Device.Plugin,
//                        method = Protocol.FromDevice.Device.Method.AppIcon,
//                        body = icon,
//                    )
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
}