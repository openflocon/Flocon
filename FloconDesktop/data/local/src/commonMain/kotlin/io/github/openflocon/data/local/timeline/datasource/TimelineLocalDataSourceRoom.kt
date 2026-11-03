package io.github.openflocon.data.local.timeline.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.RoomRawQuery
import io.github.openflocon.data.core.timeline.datasources.TimelineLocalDataSource
import io.github.openflocon.data.local.timeline.dao.TimelineDao
import io.github.openflocon.data.local.timeline.mapper.toDomain
import io.github.openflocon.data.local.timeline.mapper.toSummaryDomain
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSummaryDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

// the graph draws at most that many events of a session
private const val MAX_GRAPH_EVENTS = 50_000

internal class TimelineLocalDataSourceRoom(
    private val timelineDao: TimelineDao,
    private val dispatcherProvider: DispatcherProvider,
) : TimelineLocalDataSource {

    override fun observeTimeline(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: TimelineFilterDomainModel,
    ): Flow<PagingData<TimelineEventDomainModel>> {
        val query = buildTimelineQuery(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            filter = filter,
        )
        return Pager(
            config = PagingConfig(pageSize = 50),
            pagingSourceFactory = { timelineDao.observeTimeline(query.toRoomRawQuery()) },
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun observeTimelineEvents(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: TimelineFilterDomainModel,
    ): Flow<List<TimelineEventDomainModel>> {
        val query = buildTimelineQuery(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            filter = filter,
        )
        // same union as the list, in one page: the graph needs the whole session to place the bars
        val capped = TimelineQuery(sql = "SELECT * FROM (${query.sql}) LIMIT $MAX_GRAPH_EVENTS", params = query.params)
        return timelineDao.observeTimelineList(capped.toRoomRawQuery())
            // the query is re-run for any change of the tables, most of them do not change the events of the session
            .distinctUntilChanged()
            .map { rows -> rows.map { it.toDomain() } }
            .flowOn(dispatcherProvider.data)
    }

    override fun observeSummary(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: TimelineFilterDomainModel,
    ): Flow<TimelineSummaryDomainModel> {
        val summary = buildTimelineSummaryQuery(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            filter = filter,
        )
        val sessionCounts = buildTimelineSessionCountsQuery(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            filter = filter,
        )
        val facetCounts = buildTimelineFacetCountsQuery(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            filter = filter,
        )
        return combine(
            timelineDao.observeSummary(summary.toRoomRawQuery()),
            timelineDao.observeSessionCounts(sessionCounts.toRoomRawQuery()),
            timelineDao.observeFacetCounts(facetCounts.toRoomRawQuery()),
        ) { summaryRows, sessionRows, facetRows ->
            toSummaryDomain(summaryRows, sessionRows, facetRows)
        }
    }

    private fun TimelineQuery.toRoomRawQuery() = RoomRawQuery(
        sql = sql,
        onBindStatement = { statement ->
            params.forEachIndexed { index, arg ->
                when (arg) {
                    is String -> statement.bindText(index + 1, arg)
                    is Long -> statement.bindLong(index + 1, arg)
                    is Int -> statement.bindLong(index + 1, arg.toLong())
                    else -> statement.bindText(index + 1, arg.toString())
                }
            }
        }
    )
}
