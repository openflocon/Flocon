package io.github.openflocon.flocon.myapplication.multi

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.openflocon.flocon.myapplication.multi.database.DogDatabase
import io.github.openflocon.flocon.myapplication.multi.database.FoodDatabase
import io.github.openflocon.flocon.plugins.database.floconLogDatabaseQuery
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object Databases {
    @Volatile
    private var dogDatabase: DogDatabase? = null

    fun getDogDatabase(context: Context): DogDatabase {
        val dbName = "dogs_database"
        return dogDatabase ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                DogDatabase::class.java,
                "dogs_database"
            )
                .setQueryCallback({ sqlQuery, bindArgs -> floconLogDatabaseQuery(
                    dbName = dbName, sqlQuery = sqlQuery, bindArgs = bindArgs
                ) }, Executors.newSingleThreadExecutor())
                .fallbackToDestructiveMigration().build()
            dogDatabase = instance
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
            instance
        }
    }
}