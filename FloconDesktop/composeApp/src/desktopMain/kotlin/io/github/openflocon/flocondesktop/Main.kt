package io.github.openflocon.flocondesktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.app_icon_small
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.feedback.FeedbackDisplayerHandler
import io.github.openflocon.flocondesktop.about.AboutScreen
import io.github.openflocon.flocondesktop.window.MIN_WINDOW_HEIGHT
import io.github.openflocon.flocondesktop.window.MIN_WINDOW_WIDTH
import io.github.openflocon.flocondesktop.window.WindowStateData
import io.github.openflocon.flocondesktop.window.WindowStateSaver
import io.github.openflocon.flocondesktop.window.size
import io.github.openflocon.flocondesktop.window.windowPosition
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import java.awt.Desktop
import java.awt.Dimension

fun main() {
    System.setProperty("apple.awt.application.name", "Flocon")

    return application {
        var openAbout by remember { mutableStateOf(false) }
        val savedState = remember { WindowStateSaver.load() }
        val windowState = rememberWindowState(
            size = savedState.size(),
            position = savedState.windowPosition(),
        )

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
            App()
            FloconTray()
            // TODO later
//            FloconMenu()

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

    // TODO Later
//    if (openSettings) {
//        SettingsScreen(
//            onCloseRequest = { openSettings = false }
//        )
//    }
}

@Composable
private fun ApplicationScope.FloconTray() {
    val trayState = rememberTrayState()
    val feedbackDisplayerHandler = koinInject<FeedbackDisplayerHandler>()

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

    Tray(
        state = trayState,
        icon = painterResource(Res.drawable.app_icon_small)
    )
}
