package io.github.openflocon.flocon.myapplication.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import io.github.openflocon.flocon.myapplication.database.model.DogEntity
import io.github.openflocon.flocon.myapplication.database.model.HumanEntity
import io.github.openflocon.flocon.myapplication.database.model.HumanWithDogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DogDao {
    @Query("SELECT * FROM DogEntity")
    fun getAllDogs(): Flow<List<DogEntity>>

    @Upsert
    suspend fun insertDog(dog: DogEntity)

    @Upsert
    suspend fun insertHuman(human: HumanEntity)

    @Upsert
    suspend fun insertHumanWithDogEntity(humanWithDog: HumanWithDogEntity)
}