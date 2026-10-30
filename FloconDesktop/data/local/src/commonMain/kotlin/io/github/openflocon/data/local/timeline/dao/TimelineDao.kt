package io.github.openflocon.data.local.timeline.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import io.github.openflocon.data.local.accessibility.models.AccessibilityEventEntity
import io.github.openflocon.data.local.adbcommander.models.AdbCommandHistoryEntity
import io.github.openflocon.data.local.analytics.models.AnalyticsItemEntity
import io.github.openflocon.data.local.crashreporter.models.CrashReportEntity
import io.github.openflocon.data.local.database.models.DatabaseQueryLogEntity
import io.github.openflocon.data.local.deeplink.models.DeeplinkEntity
import io.github.openflocon.data.local.network.models.FloconNetworkCallEntity
import io.github.openflocon.data.local.table.models.TableEntity
import io.github.openflocon.data.local.table.models.TableItemEntity
import io.github.openflocon.data.local.timeline.models.TimelineFacetCountEntity
import io.github.openflocon.data.local.timeline.models.TimelineRowEntity
import io.github.openflocon.data.local.timeline.models.TimelineSessionCountEntity
import io.github.openflocon.data.local.timeline.models.TimelineSummaryRowEntity
import io.github.openflocon.data.local.webview.models.WebViewEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimelineDao {
    @RawQuery(
        observedEntities = [
            FloconNetworkCallEntity::class,
            AnalyticsItemEntity::class,
            AccessibilityEventEntity::class,
            WebViewEventEntity::class,
            CrashReportEntity::class,
            DatabaseQueryLogEntity::class,
            TableEntity::class,
            TableItemEntity::class,
            DeeplinkEntity::class,
            AdbCommandHistoryEntity::class,
        ]
    )
    fun observeTimeline(
        query: RoomRawQuery,
    ): PagingSource<Int, TimelineRowEntity>

    @RawQuery(
        observedEntities = [
            FloconNetworkCallEntity::class,
            AnalyticsItemEntity::class,
            AccessibilityEventEntity::class,
            WebViewEventEntity::class,
            CrashReportEntity::class,
            DatabaseQueryLogEntity::class,
            TableEntity::class,
            TableItemEntity::class,
            DeeplinkEntity::class,
            AdbCommandHistoryEntity::class,
        ]
    )
    fun observeSummary(
        query: RoomRawQuery,
    ): Flow<List<TimelineSummaryRowEntity>>

    @RawQuery(
        observedEntities = [
            FloconNetworkCallEntity::class,
            AnalyticsItemEntity::class,
            AccessibilityEventEntity::class,
            WebViewEventEntity::class,
            CrashReportEntity::class,
            DatabaseQueryLogEntity::class,
            TableEntity::class,
            TableItemEntity::class,
            DeeplinkEntity::class,
            AdbCommandHistoryEntity::class,
        ]
    )
    fun observeSessionCounts(
        query: RoomRawQuery,
    ): Flow<List<TimelineSessionCountEntity>>

    @RawQuery(
        observedEntities = [
            FloconNetworkCallEntity::class,
            AnalyticsItemEntity::class,
            AccessibilityEventEntity::class,
            CrashReportEntity::class,
            DatabaseQueryLogEntity::class,
        ]
    )
    fun observeFacetCounts(
        query: RoomRawQuery,
    ): Flow<List<TimelineFacetCountEntity>>
}
