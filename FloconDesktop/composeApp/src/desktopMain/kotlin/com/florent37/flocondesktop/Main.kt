package com.florent37.flocondesktop

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory

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
        title = "FloconDesktop",
        state = rememberWindowState(size = DpSize(width = 1200.dp, height = 800.dp)), // Définir la taille de la fenêtre ici
    ) {
        App()
    }
}
