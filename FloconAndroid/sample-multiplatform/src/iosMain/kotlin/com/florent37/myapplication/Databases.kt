package com.florent37.myapplication

import androidx.room.Room
import androidx.sqlite.driver.NativeSQLiteDriver
import io.github.openflocon.flocon.myapplication.multi.database.DogDatabase
import io.github.openflocon.flocon.myapplication.multi.database.FoodDatabase
import io.github.openflocon.flocon.database.room.floconRegisterDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

object Databases {
    private var dogDatabase: DogDatabase? = null

    fun getDogDatabase(): DogDatabase {
        val dbFile = "${documentDirectory()}/dog_database.db"
        return dogDatabase ?: run {
            val instance = Room.databaseBuilder<DogDatabase>(
                name = dbFile,
            ).fallbackToDestructiveMigration(
                dropAllTables = true
            ).setDriver(NativeSQLiteDriver()).build()
            dogDatabase = instance
            floconRegisterDatabase(
                displayName = "Dog Database",
                database = instance
            )
            instance
        }
    }

    private var foodDatabase: FoodDatabase? = null

    fun getFoodDatabase(): FoodDatabase {
        val dbFile = "${documentDirectory()}/food_database.db"
        return foodDatabase ?: run {
            val instance = Room.databaseBuilder<FoodDatabase>(
                name = dbFile,
            ).setDriver(NativeSQLiteDriver()).build()
            foodDatabase = instance
            floconRegisterDatabase(
                displayName = "Food Database",
                database = instance
            )
            instance
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}