package io.github.openflocon.data.local.adbcommander.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.github.openflocon.data.local.adbcommander.models.AdbCommandHistoryEntity
import io.github.openflocon.data.local.adbcommander.models.AdbFlowEntity
import io.github.openflocon.data.local.adbcommander.models.AdbFlowStepEntity
import io.github.openflocon.data.local.adbcommander.models.AdbFlowWithSteps
import io.github.openflocon.data.local.adbcommander.models.AdbSavedCommandEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdbCommanderDao {

    // Saved commands
    @Query("SELECT * FROM AdbSavedCommandEntity WHERE deviceId = :deviceId ORDER BY createdAt DESC")
    fun observeSavedCommands(deviceId: String): Flow<List<AdbSavedCommandEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedCommand(command: AdbSavedCommandEntity): Long

    @Update
    suspend fun updateSavedCommand(command: AdbSavedCommandEntity)

    @Query("DELETE FROM AdbSavedCommandEntity WHERE id = :id")
    suspend fun deleteSavedCommand(id: Long)

    // History
    @Query("SELECT * FROM AdbCommandHistoryEntity WHERE deviceId = :deviceId ORDER BY executedAt DESC LIMIT 200")
    fun observeHistory(deviceId: String): Flow<List<AdbCommandHistoryEntity>>

    @Insert
    suspend fun insertHistory(entry: AdbCommandHistoryEntity)

    @Query("DELETE FROM AdbCommandHistoryEntity WHERE deviceId = :deviceId")
    suspend fun clearHistory(deviceId: String)

    // Flows
    @Transaction
    @Query("SELECT * FROM AdbFlowEntity WHERE deviceId = :deviceId ORDER BY createdAt DESC")
    fun observeFlowsWithSteps(deviceId: String): Flow<List<AdbFlowWithSteps>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlow(flow: AdbFlowEntity): Long

    @Update
    suspend fun updateFlow(flow: AdbFlowEntity)

    @Query("DELETE FROM AdbFlowEntity WHERE id = :id")
    suspend fun deleteFlow(id: Long)

    @Query("SELECT * FROM AdbFlowEntity WHERE id = :flowId LIMIT 1")
    suspend fun getFlowById(flowId: Long): AdbFlowEntity?

    // Flow steps
    @Query("SELECT * FROM AdbFlowStepEntity WHERE flowId = :flowId ORDER BY orderIndex ASC")
    suspend fun getFlowSteps(flowId: Long): List<AdbFlowStepEntity>

    @Insert
    suspend fun insertFlowSteps(steps: List<AdbFlowStepEntity>)

    @Query("DELETE FROM AdbFlowStepEntity WHERE flowId = :flowId")
    suspend fun deleteFlowSteps(flowId: Long)

    @Transaction
    suspend fun replaceFlowSteps(flowId: Long, steps: List<AdbFlowStepEntity>) {
        deleteFlowSteps(flowId)
        insertFlowSteps(steps)
    }
}
