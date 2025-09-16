package io.github.openflocon.data.local.analytics.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.openflocon.data.local.analytics.models.AnalyticsItemEntity
import io.github.openflocon.domain.device.models.AppInstance
import io.github.openflocon.domain.device.models.AppPackageName
import io.github.openflocon.domain.device.models.DeviceId
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

    @Query(
        """
        DELETE FROM AnalyticsItemEntity
        WHERE itemId = :analyticsItemId
        AND deviceId = :deviceId
        AND packageName = :packageName
        """,
    )
    suspend fun deleteAnalyticsItem(
        deviceId: DeviceId,
        packageName: String,
        analyticsItemId: String
    )

    @Query("""
        DELETE FROM AnalyticsItemEntity 
        WHERE createdAt < (SELECT createdAt FROM AnalyticsItemEntity WHERE itemId = :analyticsItemId)
          AND deviceId = :deviceId
          AND packageName = :packageName
    """)
    suspend fun deleteBefore(deviceId: DeviceId, packageName: String, analyticsItemId: String)

    @Query(
        """
        DELETE FROM AnalyticsItemEntity
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND appInstance != :appInstance
        """
    )
    suspend fun deleteRequestOnDifferentSession(
        deviceId: String,
        packageName: String,
        appInstance: Long,
    )


}
