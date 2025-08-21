package io.github.openflocon.flocondesktop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
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
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import java.awt.Desktop
import java.awt.Dimension

private const val ACTIVATE_TRAY_NOTIFICATION = false

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
            undecorated = true,
            transparent = true,
            title = "Flocon",
            icon = painterResource(Res.drawable.app_icon_small), // Remove black behind icon
        ) {
            window.minimumSize = Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT)

            Column(
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {
                WindowDraggableArea(
                    modifier = Modifier.background(FloconTheme.colorPalette.surface)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(androidx.compose.ui.graphics.Color.Red)
                                .clickable(onClick = { exitApplication() })
                        )
                        Spacer(Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(androidx.compose.ui.graphics.Color.Yellow)
                                .clickable(onClick = { windowState.isMinimized = true })
                        )
                        Spacer(Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(androidx.compose.ui.graphics.Color.Green)
                                .clickable(onClick = { windowState.placement = WindowPlacement.Fullscreen })
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = "Pixel 6",
                            color = FloconTheme.colorPalette.onSurface,
                            style = FloconTheme.typography.bodySmall
                        )
                        FloconIcon(
                            imageVector = Icons.Outlined.ChevronLeft,
                            tint = FloconTheme.colorPalette.onSurface,
                            modifier = Modifier
                                .size(12.dp)
                                .rotate(-90f)
                        )
                        Spacer(Modifier.width(16.dp))
                        Image(
                            painter = painterResource(Res.drawable.app_icon_small),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "My Application - com.example.myapplication",
                            color = FloconTheme.colorPalette.onSurface,
                            style = FloconTheme.typography.bodySmall
                        )
                        FloconIcon(
                            imageVector = Icons.Outlined.ChevronLeft,
                            tint = FloconTheme.colorPalette.onSurface,
                            modifier = Modifier
                                .size(12.dp)
                                .rotate(-90f)
                        )
                    }
                }
                App()
            }

            if (ACTIVATE_TRAY_NOTIFICATION) {
                FloconTray()
            }
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
