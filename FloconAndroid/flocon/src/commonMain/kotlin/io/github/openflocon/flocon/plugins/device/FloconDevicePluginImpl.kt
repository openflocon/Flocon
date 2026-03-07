package io.github.openflocon.flocon.plugins.device

import io.github.openflocon.flocon.*
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.plugins.device.model.fromdevice.RegisterDeviceDataModel

actual object FloconDevice : FloconPluginFactory<FloconDeviceConfig, FloconDevicePlugin> {
    override val name: String = "Device"
    override val pluginId: String = Protocol.ToDevice.Device.Plugin
    override fun createConfig() = FloconDeviceConfig()
    override fun install(config: FloconDeviceConfig, app: FloconApp): FloconDevicePlugin {
        return FloconDevicePluginImpl(
            sender = app.client as FloconMessageSender,
            context = app.context
        )
    }
}

internal expect fun restartApp(context: FloconContext)

internal class FloconDevicePluginImpl(
    private var sender: FloconMessageSender,
    private val context: FloconContext,
) : FloconPlugin, FloconDevicePlugin {

    override fun registerWithSerial(serial: String) {
        try {
            sender.send(
                plugin = Protocol.FromDevice.Device.Plugin,
                method = Protocol.FromDevice.Device.Method.RegisterDevice,
                body = RegisterDeviceDataModel(serial).toJson().toString(),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("Device parsing error", t)
        }
    }

    override fun onMessageReceived(
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

    override fun onConnectedToServer() {
        // no op
    }
}