package io.github.openflocon.flocon.myapplication.multi.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.openflocon.flocon.myapplication.multi.database.dao.FoodDao
import io.github.openflocon.flocon.myapplication.multi.database.model.FoodEntity

@Database(
    entities = [FoodEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao

    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null

        fun getDatabase(context: Context): FoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodDatabase::class.java,
                    "food_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

