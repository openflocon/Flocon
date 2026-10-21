package com.flocon.sample.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.analytics.FloconAnalytics
import io.github.openflocon.flocon.featureflags.FloconFeatureFlags
import io.github.openflocon.flocon.database.core.FloconDatabase
import io.github.openflocon.flocon.database.core.databasePlugin
import io.github.openflocon.flocon.database.room3.register
import io.github.openflocon.flocon.database.room3.room
import io.github.openflocon.flocon.deeplinks.FloconDeeplinks
import io.github.openflocon.flocon.ktor.FloconKtorPlugin
import io.github.openflocon.flocon.myapplication.multi.DummyHttpKtorCaller
import io.github.openflocon.flocon.myapplication.multi.database.DogDatabase
import io.github.openflocon.flocon.myapplication.multi.database.FoodDatabase
import io.github.openflocon.flocon.myapplication.multi.database.model.DogEntity
import io.github.openflocon.flocon.myapplication.multi.database.initializeDatabases
import io.github.openflocon.flocon.myapplication.multi.ui.App
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeature
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeatures
import io.github.openflocon.flocon.network.core.FloconNetwork
import io.github.openflocon.flocon.crashreporter.FloconCrashReporter
import io.github.openflocon.flocon.plugins.dashboard.FloconDashboard
import io.github.openflocon.flocon.plugins.device.FloconDevice
import io.github.openflocon.flocon.plugins.files.FloconFiles
import io.github.openflocon.flocon.plugins.sharedprefs.FloconPreferences
import io.github.openflocon.flocon.tables.FloconTable
import io.github.openflocon.flocon.startFlocon
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import java.io.File
import kotlinx.coroutines.Dispatchers

fun main() {
    FloconLogger.enabled = true

    startFlocon(
        context = FloconContext(appName = "Flocon Sample Desktop", packageName = "com.flocon.sample.desktop")
    ) {
        install(FloconNetwork)
        install(FloconDashboard)
        install(FloconTable)
        install(FloconAnalytics)
        install(FloconFeatureFlags)
        install(FloconPreferences)
        install(FloconFiles)
        install(FloconDevice)
        install(FloconCrashReporter)
        install(FloconDeeplinks) {
            deeplink("flocon://home")
        }
        install(FloconDatabase) {
            room()
        }
    }

    // Initialize Ktor client with Flocon plugin for Desktop
    val ktorClient = HttpClient(CIO) {
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

    val dogDatabase = dogDatabase()
    val foodDatabase = foodDatabase()

    Flocon.databasePlugin.register(displayName = "dogs", database = dogDatabase)
    Flocon.databasePlugin.register(displayName = "food", database = foodDatabase)

    initializeDatabases(dogDatabase = dogDatabase, foodDatabase = foodDatabase)

    registerDesktopSampleFeatures(dogDatabase)

    application {
        Window(onCloseRequest = ::exitApplication, title = "Flocon Sample - Desktop") {
            App()
        }
    }
}

private fun dogDatabase(): DogDatabase {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "flocon_dogs_database.db")
    return Room.databaseBuilder<DogDatabase>(name = dbFile.absolutePath)
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

private fun foodDatabase(): FoodDatabase {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "flocon_food_database.db")
    return Room.databaseBuilder<FoodDatabase>(name = dbFile.absolutePath)
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

private fun registerDesktopSampleFeatures(dogDatabase: DogDatabase) {
    SampleFeatures.platformName = "Desktop (JVM)"

    SampleFeatures.on(SampleFeature.Database) {
        dogDatabase.dogDao().insertDog(
            DogEntity(
                id = System.currentTimeMillis(),
                name = "Flocon",
                breed = "Golden Retriever ${System.currentTimeMillis()}",
                age = 6,
                pictureUrl = "https://picsum.photos/501/500.jpg",
            )
        )
    }
    SampleFeatures.on(SampleFeature.Files) {
        // "caches" in the files plugin
        File(System.getProperty("java.io.tmpdir"), "flocon_sample.txt")
            .writeText("Written by the sample at ${System.currentTimeMillis()}")
    }

    val okHttpReason = "The OkHttp interceptor supports the JVM, but this sample uses the Ktor CIO engine"
    SampleFeatures.unavailable(SampleFeature.OkHttp, okHttpReason)
    SampleFeatures.unavailable(SampleFeature.OkHttpGzip, okHttpReason)
    SampleFeatures.unavailable(SampleFeature.WebSocket, okHttpReason)
    SampleFeatures.unavailable(SampleFeature.GraphQl, "Android only in this sample")
    SampleFeatures.unavailable(SampleFeature.Grpc, "The gRPC interceptor is Android only")
    SampleFeatures.unavailable(SampleFeature.DatabaseInMemory, "Android only in this sample")
    SampleFeatures.unavailable(SampleFeature.DataStore, "DataStore is not set up in the desktop sample")
}
