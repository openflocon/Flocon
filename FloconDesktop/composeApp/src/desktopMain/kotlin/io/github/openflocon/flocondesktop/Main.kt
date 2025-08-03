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

fun main() = application {
    startKoinApp()
    setSingletonImageLoaderFactory { context ->
        ImageLoader
            .Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }.build()
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Flocon",
        icon = painterResource(Res.drawable.app_icon_small), // Remove black behind icon
        state = rememberWindowState(
            size = DpSize(width = 1200.dp, height = 800.dp), // Définir la taille de la fenêtre ici
            position = WindowPosition(Alignment.Center),
        ),
    ) {
        window.minimumSize = Dimension(1200, 800)

        App()
    }
}
