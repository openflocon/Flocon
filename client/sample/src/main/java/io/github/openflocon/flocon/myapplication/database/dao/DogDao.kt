package io.github.openflocon.flocon.myapplication.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.openflocon.flocon.myapplication.database.model.DogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DogDao {
    @Query("SELECT * FROM DogEntity")
    fun getAllDogs(): Flow<List<DogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDog(dog: DogEntity)
}