package io.github.openflocon.flocondesktop.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import io.github.openflocon.flocondesktop.app.AboveDialogsZIndex
import io.github.openflocon.flocondesktop.app.floconRoutes
import io.github.openflocon.flocondesktop.app.floconSceneStrategies
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackHost
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.createFloconWindowState
import io.github.openflocon.navigation.FloconNavigation
import io.github.openflocon.navigation.MainFloconNavigationState
import org.koin.compose.koinInject
import org.koin.compose.scope.KoinScope
import org.koin.core.annotation.KoinExperimentalAPI

/** One window by device opened from the top bar, next to the main window. Must be called inside the Koin application and the theme. */
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
                DeviceWindowContent(deviceId = window.deviceId)
            }
        }
    }
}

/** The timeline of a device. Everything resolved in it follows [DeviceWindowTarget], the main window keeps its own device. */
@OptIn(KoinExperimentalAPI::class)
@Composable
private fun DeviceWindowContent(
    deviceId: String,
    modifier: Modifier = Modifier,
) {
    KoinScope(
        scopeDefinition = {
            createScope(scopeId = "device-window-$deviceId", qualifier = DeviceWindowScope)
                .also { it.declare(DeviceWindowTarget(deviceId = deviceId)) }
        },
    ) {
        val navigationState = koinInject<MainFloconNavigationState>()
        val sceneStrategies = remember { floconSceneStrategies() }

        Box(modifier = modifier.fillMaxSize()) {
            FloconNavigation(
                navigationState = navigationState,
                sceneStrategies = sceneStrategies,
                modifier = Modifier.fillMaxSize(),
            ) {
                floconRoutes()
            }
            FeedbackHost(modifier = Modifier.zIndex(AboveDialogsZIndex))
        }
    }
}
