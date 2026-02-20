package io.github.openflocon.flocondesktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.ApplicationScope
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
import io.github.openflocon.library.designsystem.components.escape.LocalEscapeHandlerStack
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import java.awt.Desktop
import java.awt.Dimension

private const val ACTIVATE_TRAY_NOTIFICATION = false

fun main() {
    clearTmpFiles()

    System.setProperty("apple.awt.application.name", "Flocon")

    System.setProperty("apple.awt.application.appearance", "system")
    System.setProperty("apple.laf.useScreenMenuBar", "true")

    // to force in FR :
    // Locale.setDefault(Locale.FRENCH)

    return application {
        var openAbout by remember { mutableStateOf(false) }
        val savedState = remember { WindowStateSaver.load() }
        val windowState = rememberWindowState(
            size = savedState.size(),
            position = savedState.windowPosition(),
        )

        with(Desktop.getDesktop()) {
            if (isSupported(Desktop.Action.APP_ABOUT)) {
                setAboutHandler { openAbout = true }
            }
        }

        setSingletonImageLoaderFactory { context ->
            ImageLoader
                .Builder(context)
                .components {
                    add(KtorNetworkFetcherFactory())
                }.build()
        }

        val handlers = remember { mutableStateListOf<() -> Boolean>() }

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
            onPreviewKeyEvent = {
                when (it.key) {
                    Key.Escape if it.type == KeyEventType.KeyDown -> handlers.lastOrNull()?.invoke() ?: false

                    else -> false
                }
            },
            title = "Flocon",
            icon = painterResource(Res.drawable.app_icon_small), // Remove black behind icon
        ) {
            CompositionLocalProvider(LocalEscapeHandlerStack provides handlers) {
                window.minimumSize = Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT)
                App()
                if (ACTIVATE_TRAY_NOTIFICATION) {
                    FloconTray()
                }

                if (openAbout) {
                    AboutScreen(
                        onCloseRequest = { openAbout = false }
                    )
                }
            }
        }
    }
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
