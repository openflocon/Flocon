package com.florent37.myapplication

import androidx.compose.ui.window.ComposeUIViewController
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.ktor.FloconKtorPlugin
import io.github.openflocon.flocon.myapplication.multi.DummyHttpKtorCaller
import io.github.openflocon.flocon.myapplication.multi.ui.App
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

fun MainViewController() = ComposeUIViewController(
    configure = {
        Flocon.initialize()

        val ktorClient = HttpClient(Darwin) {
            install(FloconKtorPlugin) {
                isImage = {
                    it.request.url.toString().contains("picsum.photos")
                }
            }
        }
        DummyHttpKtorCaller.initialize(ktorClient)
    }
) {
    App()
}