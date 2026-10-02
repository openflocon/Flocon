package com.flocon.sample.ios

import androidx.compose.ui.window.ComposeUIViewController
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.analytics.FloconAnalytics
import io.github.openflocon.flocon.database.core.FloconDatabase
import io.github.openflocon.flocon.database.room3.room
import io.github.openflocon.flocon.deeplinks.FloconDeeplinks
import io.github.openflocon.flocon.ktor.FloconKtorPlugin
import io.github.openflocon.flocon.myapplication.multi.DummyHttpKtorCaller
import io.github.openflocon.flocon.myapplication.multi.database.initializeDatabases
import io.github.openflocon.flocon.myapplication.multi.ui.App
import io.github.openflocon.flocon.network.core.FloconNetwork
import io.github.openflocon.flocon.startFlocon
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

fun MainViewController() = ComposeUIViewController(
    configure = {
        FloconLogger.enabled = true

        // actual class FloconContext on iOS takes no constructor parameters.
        startFlocon(FloconContext()) {
            install(FloconNetwork)
            install(FloconAnalytics)
            install(FloconDeeplinks) {
                deeplink("flocon://home")
            }
            install(FloconDatabase) {
                room()
            }
        }

        // Initialize Ktor client with Flocon plugin for iOS
        val ktorClient = HttpClient(Darwin) {
            install(FloconKtorPlugin) {
                isImage = {
                    it.request.url.toString().contains("picsum.photos")
                }
            }
        }

        SingletonImageLoader.setSafe {
            ImageLoader.Builder(context = PlatformContext.INSTANCE)
                .components {
                    add(KtorNetworkFetcherFactory(ktorClient))
                }
                .build()
        }

        // Initialize the HTTP caller
        DummyHttpKtorCaller.initialize(ktorClient)

        initializeDatabases(
            dogDatabase = Databases.getDogDatabase(),
            foodDatabase = Databases.getFoodDatabase(),
        )
    }
) {
    App()
}
