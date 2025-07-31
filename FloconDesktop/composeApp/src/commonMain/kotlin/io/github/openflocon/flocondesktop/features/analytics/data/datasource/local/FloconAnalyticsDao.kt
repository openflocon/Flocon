package io.github.openflocon.flocondesktop.features.analytics.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.model.AnalyticsItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FloconAnalyticsDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalyticsItems(
        analyticsItemEntities: List<AnalyticsItemEntity>,
    )

    @Query(
        """
        SELECT * 
        FROM AnalyticsItemEntity 
        WHERE deviceId = :deviceId
        AND analyticsTableId = :analyticsTableId 
        LIMIT 1
    """,
    )
    fun observeAnalytics(deviceId: DeviceId, analyticsTableId: String): Flow<AnalyticsItemEntity?>

    @Query(
        """
        SELECT DISTINCT analyticsTableId 
        FROM AnalyticsItemEntity 
        WHERE deviceId = :deviceId
    """,
    )
    fun observeAnalyticsTableIdsForDevice(deviceId: DeviceId): Flow<List<String>>

    @Query(
        """
        SELECT DISTINCT analyticsTableId 
        FROM AnalyticsItemEntity 
        WHERE deviceId = :deviceId
    """,
    )
    suspend fun getAnalyticsForDevice(deviceId: DeviceId): List<String>

    @Query(
        """
        SELECT * 
        FROM AnalyticsItemEntity 
        WHERE analyticsTableId = :analyticsTableId 
        AND deviceId = :deviceId
        ORDER BY createdAt ASC
    """,
    )
    fun observeAnalyticsItems(
        deviceId: DeviceId,
        analyticsTableId: String,
    ): Flow<List<AnalyticsItemEntity>>

    @Query(
        """
        DELETE FROM AnalyticsItemEntity
        WHERE analyticsTableId = :analyticsTableId
        AND deviceId = :deviceId
        """,
    )
    suspend fun deleteAnalyticsContent(
        deviceId: String,
        analyticsTableId: String,
    )
}
