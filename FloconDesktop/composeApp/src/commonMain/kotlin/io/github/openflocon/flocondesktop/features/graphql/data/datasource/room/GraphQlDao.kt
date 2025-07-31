package io.github.openflocon.flocondesktop.features.graphql.data.datasource.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.florent37.flocondesktop.features.graphql.data.datasource.room.model.GraphQlRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GraphQlDao {
    @Insert
    suspend fun insertGraphQlCall(call: GraphQlRequestEntity)

    @Transaction
    @Query(
        """
        SELECT * FROM GraphQlRequestEntity
        WHERE deviceId = :deviceId 
        ORDER BY request_startTime ASC
        """,
    )
    fun observeRequests(deviceId: String): Flow<List<GraphQlRequestEntity>>

    @Transaction
    @Query(
        """
        SELECT * FROM GraphQlRequestEntity
        WHERE deviceId = :deviceId 
        AND uuid = :requestId
        LIMIT 1
        """,
    )
    fun observeRequest(deviceId: String, requestId: String): Flow<GraphQlRequestEntity?>

    @Query("DELETE FROM GraphQlRequestEntity WHERE deviceId = :deviceId")
    suspend fun clearDeviceRequests(deviceId: String)

    @Query("DELETE FROM GraphQlRequestEntity WHERE uuid = :requestId")
    suspend fun deleteRequestById(requestId: String)

    @Transaction
    suspend fun deleteRequestsBeforeTimestamp(deviceId: String, timestamp: Long) {
        // First, get  IDs of requests to be deleted
        val idsToDelete = getRequestsIdsBeforeTimestamp(deviceId, timestamp)

        // Delete associated headers and responses
        for (callId in idsToDelete) {
            deleteRequestById(callId)
        }
    }

    @Query("SELECT uuid FROM GraphQlRequestEntity WHERE deviceId = :deviceId AND request_startTime < :timestamp")
    suspend fun getRequestsIdsBeforeTimestamp(deviceId: String, timestamp: Long): List<String>

    @Query("DELETE FROM GraphQlRequestEntity")
    suspend fun clearAllData()

    @Query(
        """
            SELECT request_startTime 
            FROM GraphQlRequestEntity 
            WHERE deviceId = :deviceId 
            AND uuid = :requestId
            LIMIT 1
        """,
    )
    suspend fun getRequestTimestamp(deviceId: String, requestId: String): Long?
}
