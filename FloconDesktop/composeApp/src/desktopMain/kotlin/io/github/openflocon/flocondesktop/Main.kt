package io.github.openflocon.flocondesktop

import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.app_icon_small
import org.jetbrains.compose.resources.painterResource
import java.awt.Dimension
import java.awt.Toolkit

fun main() = application {
    startKoinApp()
    setSingletonImageLoaderFactory { context ->
        ImageLoader
            .Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }.build()
    }

    val savedState = WindowStateSaver.load()

    val windowState = rememberWindowState(
        width = savedState?.width?.dp ?: (Toolkit.getDefaultToolkit().screenSize.width * 0.8).toInt().dp,
        height = savedState?.height?.dp ?: (Toolkit.getDefaultToolkit().screenSize.height * 0.8).toInt().dp,
        position = WindowPosition(savedState?.x?.dp ?: Dp.Unspecified, savedState?.y?.dp ?: Dp.Unspecified),
    )

    Window(
        state = windowState,
        onCloseRequest = {
            val currentSize = windowState.size
            val currentPosition = windowState.position
            WindowStateSaver.save(WindowStateData(
                width = currentSize.width.value.toInt(),
                height = currentSize.height.value.toInt(),
                x = currentPosition.x.value.toInt(),
                y = currentPosition.y.value.toInt(),
            ))

            exitApplication()
        },
        title = "Flocon",
        icon = painterResource(Res.drawable.app_icon_small), // Remove black behind icon
    ) {
        window.minimumSize = Dimension(1200, 800)

        App()
    }
}
