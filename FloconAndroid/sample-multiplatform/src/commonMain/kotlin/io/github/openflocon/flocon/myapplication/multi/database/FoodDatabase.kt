package io.github.openflocon.flocon.myapplication.multi.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import io.github.openflocon.flocon.myapplication.multi.database.dao.FoodDao
import io.github.openflocon.flocon.myapplication.multi.database.model.FoodEntity

@Database(
    entities = [FoodEntity::class],
    version = 1,
    exportSchema = false,
)
@ConstructedBy(FoodDatabaseConstructor::class)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
}

// room will generate the constructor
@Suppress("KotlinNoActualForExpect")
expect object FoodDatabaseConstructor : RoomDatabaseConstructor<FoodDatabase> {
    override fun initialize(): FoodDatabase
}