package io.github.openflocon.flocon.myapplication.multi.database

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
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