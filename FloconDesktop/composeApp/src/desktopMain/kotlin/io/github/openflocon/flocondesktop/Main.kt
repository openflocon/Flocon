package io.github.openflocon.flocondesktop

import androidx.compose.runtime.remember
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
import io.github.openflocon.flocondesktop.window.MIN_WINDOW_HEIGHT
import io.github.openflocon.flocondesktop.window.MIN_WINDOW_WIDTH
import io.github.openflocon.flocondesktop.window.WindowStateData
import io.github.openflocon.flocondesktop.window.WindowStateSaver
import io.github.openflocon.flocondesktop.window.size
import io.github.openflocon.flocondesktop.window.windowPosition
import org.jetbrains.compose.resources.painterResource
import java.awt.Dimension

fun main() = application {
    startKoinApp()
    setSingletonImageLoaderFactory { context ->
        ImageLoader
            .Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }.build()
    }

    val savedState = remember {
        WindowStateSaver.load()
    }

    val windowState = rememberWindowState(
        size = savedState.size(),
        position = savedState.windowPosition()
    )

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
                )
            )

            exitApplication()
        },
        title = "Flocon",
        icon = painterResource(Res.drawable.app_icon_small), // Remove black behind icon
    ) {
        window.minimumSize = Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT)

        App()
    }
}
