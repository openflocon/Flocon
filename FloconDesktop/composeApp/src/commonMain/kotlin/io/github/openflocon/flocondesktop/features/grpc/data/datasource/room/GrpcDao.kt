package io.github.openflocon.flocondesktop.features.grpc.data.datasource.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.florent37.flocondesktop.features.grpc.data.datasource.room.model.GrpcCallEntity
import com.florent37.flocondesktop.features.grpc.data.datasource.room.model.GrpcCallWithDetails
import com.florent37.flocondesktop.features.grpc.data.datasource.room.model.GrpcResponseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrpcDao {
    @Insert
    suspend fun insertGrpcCall(call: GrpcCallEntity)

    @Insert
    suspend fun insertGrpcResponse(response: GrpcResponseEntity)

    @Transaction
    @Query(
        """
        SELECT * FROM GrpcCallEntity
        WHERE deviceId = :deviceId 
        ORDER BY timestamp ASC
        """,
    )
    fun observeCallsWithDetails(deviceId: String): Flow<List<GrpcCallWithDetails>>

    @Transaction
    @Query(
        """
        SELECT * FROM GrpcCallEntity
        WHERE deviceId = :deviceId 
        AND callId = :callId
        LIMIT 1
        """,
    )
    fun observeCallWithDetails(deviceId: String, callId: String): Flow<GrpcCallWithDetails?>

    @Query("DELETE FROM GrpcCallEntity WHERE deviceId = :deviceId")
    suspend fun clearDeviceRequests(deviceId: String)

    @Query(
        """
        DELETE FROM GrpcResponseEntity 
        WHERE response_call_id IN (
            SELECT callId 
            FROM GrpcCallEntity 
            WHERE deviceId = :deviceId
        )
        """,
    )
    suspend fun clearDeviceResponses(deviceId: String)

    @Transaction
    suspend fun clearDeviceData(deviceId: String) {
        clearDeviceResponses(deviceId)
        clearDeviceRequests(deviceId)
    }

    @Query("DELETE FROM GrpcCallEntity WHERE callId = :requestId")
    suspend fun deleteRequestById(requestId: String)

    @Query("DELETE FROM GrpcResponseEntity WHERE response_call_id = :requestId")
    suspend fun deleteResponseById(requestId: String)

    @Transaction
    suspend fun deleteCallById(requestId: String) {
        deleteResponseById(requestId)
        deleteRequestById(requestId)
    }

    @Transaction
    suspend fun deleteCallsBeforeTimestamp(deviceId: String, timestamp: Long) {
        // First, get call IDs of requests to be deleted
        val callIdsToDelete = getCallIdsBeforeTimestamp(deviceId, timestamp)

        // Delete associated headers and responses
        for (callId in callIdsToDelete) {
            deleteCallById(callId)
        }
    }

    @Query("SELECT callId FROM GrpcCallEntity WHERE deviceId = :deviceId AND timestamp < :timestamp")
    suspend fun getCallIdsBeforeTimestamp(deviceId: String, timestamp: Long): List<String>

    @Query("DELETE FROM GrpcCallEntity")
    suspend fun deleteAllRequests()

    @Query("DELETE FROM GrpcResponseEntity")
    suspend fun deleteAllResponses()

    @Transaction
    suspend fun clearAllData() {
        deleteAllResponses()
        deleteAllRequests()
    }

    @Query(
        """
            SELECT timestamp 
            FROM GrpcCallEntity 
            WHERE deviceId = :deviceId 
            AND callId = :callId
            LIMIT 1
        """,
    )
    suspend fun getCallTimestamp(deviceId: String, callId: String): Long?
}
