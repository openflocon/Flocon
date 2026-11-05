package io.github.openflocon.flocon.device

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.encode

/**
 * Answers the device messages, without any plugin to install:
 * the app icon, and the adb serial (Android).
 */
internal class FloconDeviceHandler(
    private val sender: FloconMessageSender,
    private val context: FloconContext,
    private val encoder: FloconEncoder,
) {
    /** Sends the serial to the desktop whenever it is received. */
    fun start() {
        observeDeviceSerial(onSerial = ::registerWithSerial)
    }

    /**
     * @return true when the message was handled here, false when it must go to a plugin.
     */
    fun handle(plugin: String, method: String): Boolean {
        if (plugin != Protocol.ToDevice.Device.Plugin) return false
        return when (method) {
            Protocol.ToDevice.Device.Method.GetAppIcon -> {
                sendAppIcon()
                true
            }

            else -> false
        }
    }

    private fun sendAppIcon() {
        val icon = getAppIconBase64(context) ?: return
        sender.send(
            plugin = Protocol.FromDevice.Device.Plugin,
            method = Protocol.FromDevice.Device.Method.AppIcon,
            body = icon,
        )
    }

    private fun registerWithSerial(serial: String) {
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
}

/**
 * Calls [onSerial] when the desktop sends the device serial (adb broadcast on Android).
 */
internal expect fun observeDeviceSerial(onSerial: (serial: String) -> Unit)
