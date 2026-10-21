package com.flocon.sample.wasm

import androidx.room3.Room
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.database.core.databasePlugin
import io.github.openflocon.flocon.database.room3.register
import io.github.openflocon.flocon.myapplication.multi.database.DogDatabase
import io.github.openflocon.flocon.myapplication.multi.database.FoodDatabase
import org.w3c.dom.Worker

/**
 * SQLite runs in a Web Worker on the web, see the `worker` folder of this module
 * (an npm package, referenced in build.gradle.kts).
 */
fun createWorker(): Worker =
    js("""new Worker(new URL("sqlite-wasm-worker/worker.js", import.meta.url))""")

object Databases {
    // one worker is enough for all the databases
    private val driver by lazy { WebWorkerSQLiteDriver(createWorker()) }

    val dogDatabase: DogDatabase by lazy {
        Room.databaseBuilder<DogDatabase>(name = "dogs_database.db")
            .setDriver(driver)
            .build()
            .also { Flocon.databasePlugin.register(displayName = "dogs", database = it) }
    }

    val foodDatabase: FoodDatabase by lazy {
        Room.databaseBuilder<FoodDatabase>(name = "food_database.db")
            .setDriver(driver)
            .build()
            .also { Flocon.databasePlugin.register(displayName = "food", database = it) }
    }
}
