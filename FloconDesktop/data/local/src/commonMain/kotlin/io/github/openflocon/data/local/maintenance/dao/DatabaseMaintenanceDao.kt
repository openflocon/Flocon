package io.github.openflocon.data.local.maintenance.dao

import androidx.room.Dao
import androidx.room.Query

/**
 * Deletes the history (events captured from the apps) older than a given time.
 * Configuration data (devices, mocks, deeplinks, filters...) is never touched.
 * All times are epoch milliseconds.
 */
@Dao
interface DatabaseMaintenanceDao {

    @Query("DELETE FROM FloconNetworkCallEntity WHERE request_startTime < :before")
    suspend fun purgeNetworkCalls(before: Long)

    @Query("DELETE FROM AnalyticsItemEntity WHERE createdAt < :before")
    suspend fun purgeAnalytics(before: Long)

    @Query("DELETE FROM DeviceImageEntity WHERE time < :before")
    suspend fun purgeImages(before: Long)

    @Query("DELETE FROM AccessibilityEventEntity WHERE createdAt < :before")
    suspend fun purgeAccessibilityEvents(before: Long)

    @Query("DELETE FROM WebViewEventEntity WHERE timestamp < :before")
    suspend fun purgeWebViewEvents(before: Long)

    @Query("DELETE FROM CrashReportEntity WHERE timestamp < :before")
    suspend fun purgeCrashReports(before: Long)

    @Query("DELETE FROM DatabaseQueryLogEntity WHERE timestamp < :before")
    suspend fun purgeDatabaseQueryLogs(before: Long)

    @Query("DELETE FROM TableItemEntity WHERE createdAt < :before")
    suspend fun purgeTableItems(before: Long)
}
