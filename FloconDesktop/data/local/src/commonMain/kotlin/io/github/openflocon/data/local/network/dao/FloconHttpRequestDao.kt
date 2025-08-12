package io.github.openflocon.data.local.network.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.openflocon.data.local.network.models.FloconNetworkCallEntity
import io.github.openflocon.data.local.network.models.FloconHttpRequestEntityLite
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.coroutines.flow.Flow

@Dao
interface FloconHttpRequestDao {
    @Query(
        """
        SELECT * 
        FROM FloconNetworkCallEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
        ORDER BY request_startTime ASC
    """,
    )
    fun observeRequests(deviceId: String, packageName: String): Flow<List<FloconNetworkCallEntity>>

    @Query(
        """
        SELECT * 
        FROM FloconNetworkCallEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
        ORDER BY request_startTime ASC
    """,
    )
    fun observeRequestsLite(
        deviceId: String,
        packageName: String,
    ): Flow<List<FloconHttpRequestEntityLite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRequest(request: FloconNetworkCallEntity)

    @Query(
        """
        SELECT *
        FROM FloconNetworkCallEntity
        WHERE deviceId = :deviceId AND callId = :requestId
    """,
    )
    fun observeRequestById(
        deviceId: String,
        requestId: String,
    ): Flow<FloconNetworkCallEntity?>

    @Query("DELETE FROM FloconNetworkCallEntity")
    suspend fun clearAll()

    @Query(
        """
        DELETE FROM FloconNetworkCallEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
    """,
    )
    suspend fun clearDeviceCalls(
        deviceId: DeviceId,
        packageName: String,
    )

    @Query(
        """
        DELETE FROM FloconNetworkCallEntity
        WHERE deviceId = :deviceId
        AND callId = :requestId
    """,
    )
    suspend fun deleteRequest(
        deviceId: String,
        requestId: String,
    )

    @Query(
        """
        DELETE FROM FloconNetworkCallEntity
        WHERE deviceId = :deviceId
        AND request_startTime < (
            SELECT request_startTime 
            FROM FloconNetworkCallEntity 
            WHERE callId = :requestId 
            AND deviceId = :deviceId
            LIMIT 1
        )
    """,
    )
    suspend fun deleteRequestBefore(
        deviceId: String,
        requestId: String,
    )
}
