package io.github.openflocon.flocon.myapplication.multi

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import io.github.openflocon.flocon.database.room3.floconLogs
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.database.core.databasePlugin
import io.github.openflocon.flocon.database.room3.register
import io.github.openflocon.flocon.myapplication.multi.database.DogDatabase
import io.github.openflocon.flocon.myapplication.multi.database.FoodDatabase

object Databases {
    @Volatile
    private var dogDatabase: DogDatabase? = null

    fun getDogDatabase(context: Context): DogDatabase {
        return dogDatabase ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context = context.applicationContext,
                klass = DogDatabase::class.java,
                name = "dogs_database"
            )
                .setDriver(AndroidSQLiteDriver().floconLogs(id = "dogs_room3"))
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
            dogDatabase = instance
            Flocon.databasePlugin.register(
                id = "dogs_room3",
                displayName = "dogs",
                database = instance
            )
            instance
        }
    }

    private var foodDatabase: FoodDatabase? = null

    fun getFoodDatabase(context: Context): FoodDatabase {
        return foodDatabase ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context = context.applicationContext,
                klass = FoodDatabase::class.java,
                name = "food_database"
            )
                .setDriver(AndroidSQLiteDriver().floconLogs(id = "food_room3"))
                .build()
            foodDatabase = instance
            Flocon.databasePlugin.register(
                id = "food_room3",
                displayName = "food",
                database = instance
            )
            instance
        }
    }

    @Volatile
    private var inMemoryDogDatabase: DogDatabase? = null

    fun getInMemoryDogDatabase(context: Context): DogDatabase {
        return inMemoryDogDatabase ?: synchronized(this) {
            val instance = Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                DogDatabase::class.java,
            )
                .setDriver(AndroidSQLiteDriver().floconLogs(id = "dogs_room3_memory"))
                .build()
            inMemoryDogDatabase = instance
            Flocon.databasePlugin.register(
                id = "dogs_room3_memory",
                displayName = "inmemory_dogs",
                database = instance
            )
            instance
        }
    }
}
