package io.github.openflocon.flocon.myapplication.multi.room2

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.database.core.databasePlugin
import io.github.openflocon.flocon.database.room.extensions.floconLogs
import io.github.openflocon.flocon.database.room.register

/**
 * The only Room 2 (androidx.room) database of the sample, all the others use Room 3.
 */
@Entity(tableName = "cats")
data class CatEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val age: Int,
)

@Dao
interface CatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCat(cat: CatEntity)

    @Query("SELECT * FROM cats")
    suspend fun getCats(): List<CatEntity>
}

@Database(entities = [CatEntity::class], version = 1, exportSchema = false)
abstract class CatDatabase : RoomDatabase() {
    abstract fun catDao(): CatDao

    companion object {
        @Volatile
        private var instance: CatDatabase? = null

        fun get(context: Context): CatDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CatDatabase::class.java,
                    "cats_database",
                )
                    .floconLogs(id = "cats (Room 2)")
                    .build()
                    .also {
                        instance = it
                        Flocon.databasePlugin.register(id = "cats (Room 2)", displayName = "cats (Room 2)", database = it)
                    }
            }
        }
    }
}
