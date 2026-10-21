package com.flocon.sample.wasm

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.analytics.FloconAnalytics
import io.github.openflocon.flocon.featureflags.FloconFeatureFlags
import io.github.openflocon.flocon.deeplinks.FloconDeeplinks
import io.github.openflocon.flocon.ktor.FloconKtorPlugin
import io.github.openflocon.flocon.myapplication.multi.DummyHttpKtorCaller
import io.github.openflocon.flocon.myapplication.multi.ui.App
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeature
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeatures
import io.github.openflocon.flocon.network.core.FloconNetwork
import io.github.openflocon.flocon.plugins.dashboard.FloconDashboard
import io.github.openflocon.flocon.plugins.sharedprefs.FloconPreferences
import io.github.openflocon.flocon.tables.FloconTable
import io.github.openflocon.flocon.startFlocon
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    FloconLogger.enabled = true

    // actual class FloconContext on wasmJs takes no constructor parameters.
    startFlocon(FloconContext()) {
        install(FloconNetwork)
        install(FloconDashboard)
        install(FloconTable)
        install(FloconAnalytics)
        install(FloconFeatureFlags)
        install(FloconPreferences)
        install(FloconDeeplinks) {
            deeplink("flocon://home")
        }
    }

    // Initialize Ktor client with Flocon plugin for Wasm
    val ktorClient = HttpClient(Js) {
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

    registerWasmSampleFeatures()

    ComposeViewport(viewportContainerId = "composeTarget") {
        App()
    }
}

private fun registerWasmSampleFeatures() {
    SampleFeatures.platformName = "Wasm"

    SampleFeatures.unavailable(SampleFeature.OkHttp, "The OkHttp interceptor is Android and JVM only")
    SampleFeatures.unavailable(SampleFeature.OkHttpGzip, "The OkHttp interceptor is Android and JVM only")
    SampleFeatures.unavailable(SampleFeature.WebSocket, "The OkHttp WebSocket logging is Android and JVM only")
    SampleFeatures.unavailable(SampleFeature.GraphQl, "Android only in this sample")
    SampleFeatures.unavailable(SampleFeature.Grpc, "The gRPC interceptor is Android only")
    SampleFeatures.unavailable(
        SampleFeature.DatabaseRoom3,
        "Room 3 supports Wasm, but this sample has no web SQLite driver (nor KSP for wasmJs) yet",
    )
    SampleFeatures.unavailable(SampleFeature.DatabaseRoom2, "Room 2 does not support Wasm")
    SampleFeatures.unavailable(SampleFeature.DatabaseInMemory, "Same as Room 3: no web SQLite driver in this sample yet")
    SampleFeatures.unavailable(SampleFeature.DataStore, "DataStore is not available on Wasm")
    SampleFeatures.unavailable(SampleFeature.Files, "No file browsing in the browser")
    SampleFeatures.unavailable(SampleFeature.CrashReporter, "Crashes are not captured nor stored in the browser")
}
