package com.flocon.sample.ios

import androidx.room3.Room
import androidx.sqlite.driver.NativeSQLiteDriver
import io.github.openflocon.flocon.database.room3.floconLogs
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.database.core.databasePlugin
import io.github.openflocon.flocon.database.room3.register
import io.github.openflocon.flocon.myapplication.multi.database.DogDatabase
import io.github.openflocon.flocon.myapplication.multi.database.FoodDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

object Databases {
    private var dogDatabase: DogDatabase? = null

    fun getDogDatabase(): DogDatabase {
        val dbFile = "${documentDirectory()}/dog_database.db"
        return dogDatabase ?: run {
            val instance = Room.databaseBuilder<DogDatabase>(name = dbFile)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .setDriver(NativeSQLiteDriver().floconLogs(id = "dogs_ios"))
                .build()
            dogDatabase = instance
            Flocon.databasePlugin.register(id = "dogs_ios", displayName = "Dog Database", database = instance)
            instance
        }
    }

    private var foodDatabase: FoodDatabase? = null

    fun getFoodDatabase(): FoodDatabase {
        val dbFile = "${documentDirectory()}/food_database.db"
        return foodDatabase ?: run {
            val instance = Room.databaseBuilder<FoodDatabase>(name = dbFile)
                .setDriver(NativeSQLiteDriver().floconLogs(id = "food_ios"))
                .build()
            foodDatabase = instance
            Flocon.databasePlugin.register(id = "food_ios", displayName = "Food Database", database = instance)
            instance
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}
