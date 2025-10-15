package io.github.openflocon.flocon.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.openflocon.flocon.myapplication.database.dao.DogDao
import io.github.openflocon.flocon.myapplication.database.model.DogEntity
import io.github.openflocon.flocon.myapplication.database.model.HumanEntity
import io.github.openflocon.flocon.myapplication.database.model.HumanWithDogEntity

@Database(
    entities = [
        DogEntity::class,
        HumanEntity::class,
        HumanWithDogEntity::class,
    ],
    version = 3,
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
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}