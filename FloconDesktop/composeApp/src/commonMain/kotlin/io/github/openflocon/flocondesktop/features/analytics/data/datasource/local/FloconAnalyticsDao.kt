package io.github.openflocon.flocondesktop.features.analytics.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.openflocon.domain.device.models.DeviceId
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
        AND packageName = :packageName
        LIMIT 1
    """,
    )
    fun observeAnalytics(
        deviceId: DeviceId,
        packageName: String,
        analyticsTableId: String,
    ): Flow<AnalyticsItemEntity?>

    @Query(
        """
        SELECT DISTINCT analyticsTableId 
        FROM AnalyticsItemEntity 
        WHERE deviceId = :deviceId
        AND packageName = :packageName
    """,
    )
    fun observeAnalyticsTableIdsForDevice(
        deviceId: DeviceId,
        packageName: String,
    ): Flow<List<String>>

    @Query(
        """
        SELECT DISTINCT analyticsTableId 
        FROM AnalyticsItemEntity 
        WHERE deviceId = :deviceId
        AND packageName = :packageName
    """,
    )
    suspend fun getAnalyticsForDevice(
        deviceId: DeviceId,
        packageName: String,
    ): List<String>

    @Query(
        """
        SELECT * 
        FROM AnalyticsItemEntity 
        WHERE analyticsTableId = :analyticsTableId 
        AND deviceId = :deviceId
        AND packageName = :packageName
        ORDER BY createdAt ASC
    """,
    )
    fun observeAnalyticsItems(
        deviceId: DeviceId,
        packageName: String,
        analyticsTableId: String,
    ): Flow<List<AnalyticsItemEntity>>

    @Query(
        """
        DELETE FROM AnalyticsItemEntity
        WHERE analyticsTableId = :analyticsTableId
        AND deviceId = :deviceId
        AND packageName = :packageName
        """,
    )
    suspend fun deleteAnalyticsContent(
        deviceId: String,
        packageName: String,
        analyticsTableId: String,
    )
}
