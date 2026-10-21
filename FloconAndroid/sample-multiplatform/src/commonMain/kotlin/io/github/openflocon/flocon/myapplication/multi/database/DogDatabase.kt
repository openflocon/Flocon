package io.github.openflocon.flocon.myapplication.multi.database

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import io.github.openflocon.flocon.myapplication.multi.database.dao.DogDao
import io.github.openflocon.flocon.myapplication.multi.database.model.DogEntity
import io.github.openflocon.flocon.myapplication.multi.database.model.HumanEntity
import io.github.openflocon.flocon.myapplication.multi.database.model.HumanWithDogEntity

@Database(
    entities = [
        DogEntity::class,
        HumanEntity::class,
        HumanWithDogEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
@ConstructedBy(DogDatabaseConstructor::class)
abstract class DogDatabase : RoomDatabase() {
    abstract fun dogDao(): DogDao
}

// room will generate the constructor (not on wasmJs)
@Suppress("KotlinNoActualForExpect", "NO_ACTUAL_FOR_EXPECT")
expect object DogDatabaseConstructor : RoomDatabaseConstructor<DogDatabase> {
    override fun initialize(): DogDatabase
}