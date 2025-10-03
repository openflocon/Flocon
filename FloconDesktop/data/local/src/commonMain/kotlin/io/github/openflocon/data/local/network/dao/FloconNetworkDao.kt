package io.github.openflocon.data.local.network.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import io.github.openflocon.data.local.network.models.FloconNetworkCallEntity
import io.github.openflocon.domain.device.models.AppPackageName
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.coroutines.flow.Flow

@Dao
interface FloconNetworkDao {
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

    @RawQuery(observedEntities = [FloconNetworkCallEntity::class])
    suspend fun observeRequestsRaw(query: RoomRawQuery): List<FloconNetworkCallEntity>

    @Query(
        """
        SELECT * 
        FROM FloconNetworkCallEntity 
        WHERE callId IN (:ids)
        AND deviceId = :deviceId 
        AND packageName = :packageName
        ORDER BY request_startTime ASC
    """,
    )
    suspend fun getRequests(ids: List<String>, deviceId: DeviceId, packageName: AppPackageName) : List<FloconNetworkCallEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRequest(request: FloconNetworkCallEntity)

    @Query(
        """
        SELECT *
        FROM FloconNetworkCallEntity
        WHERE deviceId = :deviceId 
        AND packageName = :packageName 
        AND callId = :callId
    """,
    )
    fun observeCallById(
        deviceId: String,
        packageName: String,
        callId: String,
    ): Flow<FloconNetworkCallEntity?>

    @Query(
        """
        SELECT *
        FROM FloconNetworkCallEntity
        WHERE deviceId = :deviceId 
        AND packageName = :packageName 
        AND callId = :callId
    """,
    )
    suspend fun getCallById(
        deviceId: String,
        packageName: String,
        callId: String,
    ): FloconNetworkCallEntity?

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
        AND packageName = :packageName
        AND callId = :callId
    """,
    )
    suspend fun deleteRequest(
        deviceId: String,
        packageName: String,
        callId: String,
    )

    @Query(
        """
        DELETE FROM FloconNetworkCallEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND appInstance != :appInstance
    """,
    )
    suspend fun deleteRequestOnDifferentSession(
        deviceId: String,
        packageName: String,
        appInstance: Long,
    )

    @Query(
        """
        DELETE FROM FloconNetworkCallEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND request_startTime < (
            SELECT request_startTime 
            FROM FloconNetworkCallEntity 
            WHERE callId = :callId 
            AND deviceId = :deviceId
            LIMIT 1
        )
    """,
    )
    suspend fun deleteRequestBefore(
        deviceId: String,
        packageName: String,
        callId: String,
    )
}
