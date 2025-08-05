package io.github.openflocon.flocondesktop

import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
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

    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val screenWidth = screenSize.width
    val screenHeight = screenSize.height

    val windowWidth = (screenWidth * 0.8).toInt()
    val windowHeight = (screenHeight * 0.8).toInt()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Flocon",
        icon = painterResource(Res.drawable.app_icon_small), // Remove black behind icon
        state = rememberWindowState(
            size = DpSize(width = windowWidth.dp, height = windowHeight.dp),
        ),
    ) {
        window.minimumSize = Dimension(windowWidth, windowHeight)

        App()
    }
}
