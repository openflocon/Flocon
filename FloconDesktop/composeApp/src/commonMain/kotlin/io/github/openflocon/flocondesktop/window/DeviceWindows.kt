package io.github.openflocon.flocondesktop.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.createFloconWindowState
import org.koin.compose.koinInject
import org.koin.compose.scope.KoinScope
import org.koin.core.annotation.KoinExperimentalAPI

/** One window by device opened from the top bar, next to the main window. Must be called inside the Koin application. */
@Composable
fun DeviceWindows() {
    val controller = koinInject<DeviceWindowsController>()
    val windows by controller.windows.collectAsState()

    windows.forEach { window ->
        key(window.deviceId) {
            val state = remember { createFloconWindowState() }

            FloconWindow(
                title = window.title,
                state = state,
                onCloseRequest = { controller.close(window.deviceId) },
            ) {
                DeviceWindowContent(deviceId = window.deviceId, deviceName = window.title)
            }
        }
    }
}

/** All the plugins of a device. Everything resolved in it follows [DeviceWindowTarget], the main window keeps its own device. */
@OptIn(KoinExperimentalAPI::class)
@Composable
private fun DeviceWindowContent(
    deviceId: String,
    deviceName: String,
) {
    KoinScope(
        scopeDefinition = {
            createScope(scopeId = "device-window-$deviceId", qualifier = DeviceWindowScope)
                .also { it.declare(DeviceWindowTarget(deviceId = deviceId, deviceName = deviceName)) }
        },
    ) {
        DeviceWindowScreen(deviceName = deviceName)
    }
}
