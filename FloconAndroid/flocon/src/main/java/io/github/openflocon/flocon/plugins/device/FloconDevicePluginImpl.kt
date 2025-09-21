package io.github.openflocon.flocon.plugins.device

import android.content.Context
import com.jakewharton.processphoenix.ProcessPhoenix
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.device.model.fromdevice.RegisterDeviceDataModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

private val FLOCON_DISPLAY_FPS_CONFIG = "flocon_display_fps_config.json"

internal class FloconDevicePluginImpl(
    private var sender: FloconMessageSender,
    private val context: Context,
) : FloconDevicePlugin {

    private val fpsScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val fpsOverlay = FpsDisplayer(context)

    init {
        val displayFps = loadDisplayFps()
        setupDisplayFps(displayFps)
    }

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

    private fun setupDisplayFps(display: Boolean) {
        fpsScope.launch {
            if (display) {
                fpsOverlay.start()
            } else {
                fpsOverlay.stop()
            }
        }
    }

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
        sender: FloconMessageSender
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

            Protocol.ToDevice.Device.Method.DisplayFps -> {
                val display = messageFromServer.body.toBoolean()
                println("FLOCON_FLOCON : display fps : $display")
                saveDisplayFps(display)
                setupDisplayFps(display)
            }
        }
    }

    override fun onConnectedToServer(sender: FloconMessageSender) {
        // no op
    }

    private fun loadDisplayFps(): Boolean {
        return try {
            val file = File(context.filesDir, FLOCON_DISPLAY_FPS_CONFIG)
            if (!file.exists()) {
                return false
            }

            val text = FileInputStream(file).use {
                it.readBytes().toString(Charsets.UTF_8)
            }
            return text == "true"
        } catch (t: Throwable) {
            FloconLogger.logError("issue in loadDisplayFps", t)
            false
        }
    }

    private fun saveDisplayFps(displayFps: Boolean) {
        try {
            val file = File(context.filesDir, FLOCON_DISPLAY_FPS_CONFIG)
            if (!displayFps) {
                file.delete()
            } else {
                FileOutputStream(file).use {
                    it.write("true".toByteArray())
                }
            }
        } catch (t: Throwable) {
            FloconLogger.logError("issue in saveDisplayFps", t)
        }
    }

}