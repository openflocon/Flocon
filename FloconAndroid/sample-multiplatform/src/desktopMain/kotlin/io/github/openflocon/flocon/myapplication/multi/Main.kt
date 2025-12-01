package io.github.openflocon.flocon.myapplication.multi

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.ktor.FloconKtorPlugin
import io.github.openflocon.flocon.myapplication.multi.database.DogDatabase
import io.github.openflocon.flocon.myapplication.multi.database.FoodDatabase
import io.github.openflocon.flocon.myapplication.multi.database.initializeDatabases
import io.github.openflocon.flocon.myapplication.multi.ui.App
import io.github.openflocon.flocon.plugins.database.floconRegisterDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.Dispatchers
import java.io.File

fun main() {
    Flocon.initialize()
    // Initialize Ktor client with Flocon plugin for Desktop
    val ktorClient = HttpClient(CIO) {
        install(FloconKtorPlugin) {
            isImage = {
                it.request.url.toString().contains("picsum.photos")
            }
            /*
            shouldLog = {
                val url = it.url.toString()
                println("url: $url")
                url.contains("1").not()
            }
             */
        }
    }

    SingletonImageLoader.setSafe {
        ImageLoader.Builder(context = PlatformContext.INSTANCE)
            .components {
                add(
                    coil3.network.ktor3.KtorNetworkFetcherFactory(ktorClient)
                )
            }
            .build()
    }

    // Initialize the HTTP caller
    DummyHttpKtorCaller.initialize(ktorClient)

    val dogDatabase = getDogsDatabase()
    val foodDatabase = getFoodDatabase()
    initializeDatabases(
        dogDatabase = dogDatabase,
        foodDatabase = foodDatabase,
    )

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Flocon Multi App - Desktop"
        ) {
            App()
        }
    }
}


fun getDogsDatabase(): DogDatabase {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "flocon_dogs_database.db")
    val builder = Room.databaseBuilder<DogDatabase>(
        name = dbFile.absolutePath,
    )

    floconRegisterDatabase(
        displayName = "dogs",
        absolutePath = dbFile.absolutePath,
    )

    return builder
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

fun getFoodDatabase(): FoodDatabase {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "flocon_food_database.db")
    val builder = Room.databaseBuilder<FoodDatabase>(
        name = dbFile.absolutePath,
    )

    floconRegisterDatabase(
        displayName = "food",
        absolutePath = dbFile.absolutePath,
    )

    return builder
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}