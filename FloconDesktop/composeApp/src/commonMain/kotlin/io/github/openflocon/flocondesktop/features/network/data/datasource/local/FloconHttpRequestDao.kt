package io.github.openflocon.flocondesktop.features.network.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.openflocon.domain.models.DeviceId
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.model.FloconHttpRequestEntity
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.model.FloconHttpRequestEntityLite
import kotlinx.coroutines.flow.Flow

@Dao
interface FloconHttpRequestDao {
    @Query(
        """
        SELECT * 
        FROM FloconHttpRequestEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
        ORDER BY startTime ASC
    """,
    )
    fun observeRequests(deviceId: String, packageName: String): Flow<List<FloconHttpRequestEntity>>

    @Query(
        """
        SELECT * 
        FROM FloconHttpRequestEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
        ORDER BY startTime ASC
    """,
    )
    fun observeRequestsLite(
        deviceId: String,
        packageName: String,
    ): Flow<List<FloconHttpRequestEntityLite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRequest(request: FloconHttpRequestEntity)

    @Query(
        """
        SELECT *
        FROM FloconHttpRequestEntity
        WHERE deviceId = :deviceId AND uuid = :requestId
    """,
    )
    fun observeRequestById(
        deviceId: String,
        requestId: String,
    ): Flow<FloconHttpRequestEntity?>

    @Query("DELETE FROM FloconHttpRequestEntity")
    suspend fun clearAll()

    @Query(
        """
        DELETE FROM FloconHttpRequestEntity
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
        DELETE FROM FloconHttpRequestEntity
        WHERE deviceId = :deviceId
        AND uuid = :requestId
    """,
    )
    suspend fun deleteRequest(
        deviceId: String,
        requestId: String,
    )

    @Query(
        """
        DELETE FROM FloconHttpRequestEntity
        WHERE deviceId = :deviceId
        AND startTime < (
            SELECT startTime 
            FROM FloconHttpRequestEntity 
            WHERE uuid = :requestId 
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
