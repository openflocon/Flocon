package io.github.openflocon.flocon.myapplication.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.openflocon.flocon.myapplication.database.model.FoodEntity
import kotlinx.coroutines.flow.Flow

// DAO pour la nourriture
@Dao
interface FoodDao {
    @Query("SELECT * FROM FoodEntity")
    fun getAllFoods(): Flow<List<FoodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodEntity)
}