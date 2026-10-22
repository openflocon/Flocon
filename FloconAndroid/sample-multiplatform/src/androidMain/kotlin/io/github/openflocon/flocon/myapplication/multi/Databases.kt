package io.github.openflocon.flocon.myapplication.multi

import android.content.Context
import androidx.room3.Room
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
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
            dogDatabase = instance
            Flocon.databasePlugin.register(
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
            ).build()
            foodDatabase = instance
            Flocon.databasePlugin.register(
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
            Flocon.databasePlugin.register(
                displayName = "inmemory_dogs",
                database = instance
            )
            instance
        }
    }
}
