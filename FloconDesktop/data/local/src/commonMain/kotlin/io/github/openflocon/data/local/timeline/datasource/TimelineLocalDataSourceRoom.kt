package io.github.openflocon.data.local.timeline.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.RoomRawQuery
import io.github.openflocon.data.core.timeline.datasources.TimelineLocalDataSource
import io.github.openflocon.data.local.timeline.dao.TimelineDao
import io.github.openflocon.data.local.timeline.mapper.toDomain
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class TimelineLocalDataSourceRoom(
    private val timelineDao: TimelineDao,
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
