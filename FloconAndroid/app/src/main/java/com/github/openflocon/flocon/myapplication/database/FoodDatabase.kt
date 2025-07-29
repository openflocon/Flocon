package com.github.openflocon.flocon.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.openflocon.flocon.myapplication.database.dao.FoodDao
import com.github.openflocon.flocon.myapplication.database.model.FoodEntity

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
                    "food_database" // Nom du fichier de la base de données FoodEntity
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}