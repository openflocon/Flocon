package io.github.openflocon.flocon.plugins.device

import android.content.Context
import com.jakewharton.processphoenix.ProcessPhoenix
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.device.model.fromdevice.RegisterDeviceDataModel

internal class FloconDevicePluginImpl(
    private var sender: FloconMessageSender,
    private val context: Context,
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
        messageFromServer: FloconMessageFromServer,
    ) {
        when (messageFromServer.method) {
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
                ProcessPhoenix.triggerRebirth(context)
            }
        }
    }

    override fun onConnectedToServer() {
        // no op
    }
}