package com.github.openflocon.flocon.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.openflocon.flocon.myapplication.database.dao.DogDao
import com.github.openflocon.flocon.myapplication.database.model.DogEntity

@Database(
    entities = [DogEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class DogDatabase : RoomDatabase() {
    abstract fun dogDao(): DogDao

    companion object {
        @Volatile
        private var INSTANCE: DogDatabase? = null

        fun getDatabase(context: Context): DogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DogDatabase::class.java,
                    "dogs_database" // Nom du fichier de la base de données Dogs
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}