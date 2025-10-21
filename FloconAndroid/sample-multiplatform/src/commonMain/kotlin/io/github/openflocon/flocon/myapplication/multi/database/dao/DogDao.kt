package io.github.openflocon.flocon.myapplication.multi.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.openflocon.flocon.myapplication.multi.database.model.DogEntity
import io.github.openflocon.flocon.myapplication.multi.database.model.HumanEntity
import io.github.openflocon.flocon.myapplication.multi.database.model.HumanWithDogEntity
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

