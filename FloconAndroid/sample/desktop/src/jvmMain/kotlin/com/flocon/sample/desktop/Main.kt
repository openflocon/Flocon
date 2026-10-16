package com.flocon.sample.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.analytics.FloconAnalytics
import io.github.openflocon.flocon.database.core.FloconDatabase
import io.github.openflocon.flocon.database.room3.floconRegisterDatabase
import io.github.openflocon.flocon.database.room3.room
import io.github.openflocon.flocon.deeplinks.FloconDeeplinks
import io.github.openflocon.flocon.ktor.FloconKtorPlugin
import io.github.openflocon.flocon.myapplication.multi.DummyHttpKtorCaller
import io.github.openflocon.flocon.myapplication.multi.database.DogDatabase
import io.github.openflocon.flocon.myapplication.multi.database.FoodDatabase
import io.github.openflocon.flocon.myapplication.multi.database.initializeDatabases
import io.github.openflocon.flocon.myapplication.multi.ui.App
import io.github.openflocon.flocon.network.core.FloconNetwork
import io.github.openflocon.flocon.startFlocon
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.Dispatchers
import java.io.File

fun main() {
    FloconLogger.enabled = true

    startFlocon(
        context = FloconContext(appName = "Flocon Sample Desktop", packageName = "com.flocon.sample.desktop")
    ) {
        install(FloconNetwork)
        install(FloconAnalytics)
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

    floconRegisterDatabase(displayName = "dogs", database = dogDatabase)
    floconRegisterDatabase(displayName = "food", database = foodDatabase)

    initializeDatabases(dogDatabase = dogDatabase, foodDatabase = foodDatabase)

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
