package io.github.openflocon.data.local.commands.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import io.github.openflocon.data.local.commands.model.AdbCommandEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdbCommandDao {

    @Upsert
    suspend fun upsertAll(commands: List<AdbCommandEntity>)

    @Delete
    suspend fun delete(command: AdbCommandEntity)

    @Query(
        """
        SELECT * 
        FROM AdbCommandEntity
    """
    )
    fun getAll(): Flow<List<AdbCommandEntity>>

}
