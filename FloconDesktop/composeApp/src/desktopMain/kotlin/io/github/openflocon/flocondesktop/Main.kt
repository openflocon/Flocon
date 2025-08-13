package io.github.openflocon.flocondesktop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.app_icon_small
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayerHandler
import io.github.openflocon.flocondesktop.main.ui.settings.SettingsScreen
import io.github.openflocon.flocondesktop.about.AboutScreen
import io.github.openflocon.flocondesktop.window.MIN_WINDOW_HEIGHT
import io.github.openflocon.flocondesktop.window.MIN_WINDOW_WIDTH
import io.github.openflocon.flocondesktop.window.WindowStateData
import io.github.openflocon.flocondesktop.window.WindowStateSaver
import io.github.openflocon.flocondesktop.window.size
import io.github.openflocon.flocondesktop.window.windowPosition
import org.jetbrains.compose.resources.painterResource
import java.awt.Desktop
import java.awt.Dimension

fun main() {
    System.setProperty("apple.awt.application.name", "Flocon")

    return application {
        var openAbout by remember { mutableStateOf(false) }

        Desktop.getDesktop().setAboutHandler {
            openAbout = true
        }

        setSingletonImageLoaderFactory { context ->
            ImageLoader
                .Builder(context)
                .components {
                    add(KtorNetworkFetcherFactory())
                }.build()
        }

        LaunchedEffect(Unit) {
            feedbackDisplayerHandler.notificationsToDisplay
                .collect { notification ->
                    trayState.sendNotification(
                        Notification(
                            title = notification.title,
                            message = notification.message,
                            type = when (notification.type) {
                                FeedbackDisplayer.NotificationType.None -> Notification.Type.None
                                FeedbackDisplayer.NotificationType.Info -> Notification.Type.Info
                                FeedbackDisplayer.NotificationType.Warning -> Notification.Type.Warning
                                FeedbackDisplayer.NotificationType.Error -> Notification.Type.Error
                            }
                        )
                    )
                }
        }

        FloconTray(trayState)
        Window(
            state = windowState,
            onCloseRequest = {
                val currentSize = windowState.size
                val currentPosition = windowState.position
                WindowStateSaver.save(
                    WindowStateData(
                        width = currentSize.width.value.toInt(),
                        height = currentSize.height.value.toInt(),
                        x = currentPosition.x.value.toInt(),
                        y = currentPosition.y.value.toInt(),
                    ),
                )

                exitApplication()
            },
            title = "Flocon",
            icon = painterResource(Res.drawable.app_icon_small), // Remove black behind icon
        ) {
            window.minimumSize = Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT)
            // TODO later
//            FloconMenu()
            App()

            App()

            if (openAbout) {
                AboutScreen(
                    onCloseRequest = { openAbout = false }
                )
            }
        }
    }
}

@Composable
private fun FrameWindowScope.FloconMenu() {
    var openSettings by remember { mutableStateOf(false) }

    MenuBar {
        Menu(
            text = "Settings"
        ) {
            Item(
                text = "Open",
                onClick = {
                    openSettings = true
                }
            )
        }
    }

    if (openSettings) {
        SettingsScreen(
            onCloseRequest = { openSettings = false }
        )
    }
}

@Composable
private fun ApplicationScope.FloconTray(trayState: TrayState) {
    Tray(
        state = trayState,
        icon = painterResource(Res.drawable.app_icon_small)
    )
}
