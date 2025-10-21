package io.github.openflocon.flocon.myapplication.multi

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.ktor.FloconKtorPlugin
import io.github.openflocon.flocon.myapplication.multi.ui.App
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

fun main() {
    Flocon.initialize()
    // Initialize Ktor client with Flocon plugin for Desktop
    val ktorClient = HttpClient(CIO) {
        install(FloconKtorPlugin)
    }

    // Initialize the HTTP caller
    DummyHttpKtorCaller.initialize(ktorClient)

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Flocon Multi App - Desktop"
        ) {
            App()
        }
    }
}

