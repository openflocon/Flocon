package io.github.openflocon.data.local.analytics.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.openflocon.data.local.analytics.models.AnalyticsEventCountEntity
import io.github.openflocon.data.local.analytics.models.AnalyticsItemEntity
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
        AND itemId = :analyticsItemId 
        AND packageName = :packageName
        LIMIT 1
    """,
    )
    fun observeAnalyticsItemById(
        deviceId: String,
        packageName: String,
        analyticsItemId: String,
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
        -- Optional filter
        AND (:filter IS NULL 
            OR eventName LIKE '%' || :filter || '%' 
            OR createdAtFormatted LIKE '%' || :filter || '%' 
            OR propertiesColumnsNames LIKE '%' || :filter || '%' 
            OR propertiesValues LIKE '%' || :filter || '%'
        )
        ORDER BY createdAt ASC
    """,
    )
    fun observeAnalyticsItems(
        deviceId: DeviceId,
        packageName: String,
        analyticsTableId: String,
        filter: String?,
    ): PagingSource<Int, AnalyticsItemEntity>

    @Query(
        """
        SELECT * 
        FROM AnalyticsItemEntity 
        WHERE analyticsTableId = :analyticsTableId 
        AND deviceId = :deviceId
        AND packageName = :packageName
        -- Optional filter
        AND (:filter IS NULL 
            OR eventName LIKE '%' || :filter || '%' 
            OR createdAtFormatted LIKE '%' || :filter || '%' 
            OR propertiesColumnsNames LIKE '%' || :filter || '%' 
            OR propertiesValues LIKE '%' || :filter || '%'
        )
        ORDER BY createdAt ASC
    """,
    )
    suspend fun getAnalyticsItems(
        deviceId: DeviceId,
        packageName: String,
        analyticsTableId: String,
        filter: String?,
    ): List<AnalyticsItemEntity>

    @Query(
        """
        SELECT * 
        FROM AnalyticsItemEntity 
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND (:analyticsTableId IS NULL OR analyticsTableId = :analyticsTableId)
        AND (:appInstance IS NULL OR appInstance = :appInstance)
        AND (:sinceTimestamp IS NULL OR createdAt >= :sinceTimestamp)
        AND (:filterOnEventNames = 0 OR eventName IN (:eventNames))
        AND (:filter IS NULL 
            OR eventName LIKE '%' || :filter || '%' 
            OR createdAtFormatted LIKE '%' || :filter || '%' 
            OR propertiesColumnsNames LIKE '%' || :filter || '%' 
            OR propertiesValues LIKE '%' || :filter || '%'
        )
        ORDER BY CASE WHEN :newestFirst THEN createdAt END DESC, createdAt ASC
        LIMIT :limit
    """,
    )
    suspend fun searchAnalyticsItems(
        deviceId: DeviceId,
        packageName: String,
        analyticsTableId: String?,
        appInstance: Long?,
        sinceTimestamp: Long?,
        filterOnEventNames: Boolean,
        eventNames: List<String>,
        filter: String?,
        newestFirst: Boolean,
        limit: Int,
    ): List<AnalyticsItemEntity>

    @Query(
        """
        SELECT eventName, COUNT(*) AS count
        FROM AnalyticsItemEntity 
        WHERE deviceId = :deviceId
        AND packageName = :packageName
        AND (:analyticsTableId IS NULL OR analyticsTableId = :analyticsTableId)
        AND (:appInstance IS NULL OR appInstance = :appInstance)
        AND (:sinceTimestamp IS NULL OR createdAt >= :sinceTimestamp)
        AND (:filterOnEventNames = 0 OR eventName IN (:eventNames))
        AND (:filter IS NULL 
            OR eventName LIKE '%' || :filter || '%' 
            OR createdAtFormatted LIKE '%' || :filter || '%' 
            OR propertiesColumnsNames LIKE '%' || :filter || '%' 
            OR propertiesValues LIKE '%' || :filter || '%'
        )
        GROUP BY eventName
        ORDER BY count DESC, eventName ASC
    """,
    )
    suspend fun countAnalyticsItemsByEventName(
        deviceId: DeviceId,
        packageName: String,
        analyticsTableId: String?,
        appInstance: Long?,
        sinceTimestamp: Long?,
        filterOnEventNames: Boolean,
        eventNames: List<String>,
        filter: String?,
    ): List<AnalyticsEventCountEntity>

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

    @Query(
        """
        DELETE FROM AnalyticsItemEntity 
        WHERE createdAt < (SELECT createdAt FROM AnalyticsItemEntity WHERE itemId = :analyticsItemId)
          AND deviceId = :deviceId
          AND packageName = :packageName
    """
    )
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
