package io.github.openflocon.flocon.myapplication.multi

import android.content.Context
import androidx.room.Room
import io.github.openflocon.flocon.database.room.floconRegisterDatabase
import io.github.openflocon.flocon.myapplication.multi.database.DogDatabase
import io.github.openflocon.flocon.myapplication.multi.database.FoodDatabase
import java.util.concurrent.Executors

object Databases {
    @Volatile
    private var dogDatabase: DogDatabase? = null

    fun getDogDatabase(context: Context): DogDatabase {
        return dogDatabase ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                DogDatabase::class.java,
                "dogs_database"
            )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
            dogDatabase = instance
            floconRegisterDatabase(
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
                context.applicationContext,
                FoodDatabase::class.java,
                "food_database"
            ).build()
            foodDatabase = instance
            floconRegisterDatabase(
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
            ).build()
            inMemoryDogDatabase = instance
            floconRegisterDatabase(
                displayName = "inmemory_dogs",
                database = instance
            )
            instance
        }
    }
}