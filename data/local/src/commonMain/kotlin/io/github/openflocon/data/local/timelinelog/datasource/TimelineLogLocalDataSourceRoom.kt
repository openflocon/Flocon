package io.github.openflocon.data.local.timelinelog.datasource

import io.github.openflocon.data.core.timelinelog.datasource.TimelineLogLocalDataSource
import io.github.openflocon.data.local.timelinelog.dao.TimelineLogDao
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timelinelog.models.TimelineLogDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class TimelineLogLocalDataSourceRoom(
    private val timelineLogDao: TimelineLogDao,
) : TimelineLogLocalDataSource {

    override suspend fun insert(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        log: TimelineLogDomainModel,
    ) {
        timelineLogDao.insertAndTrim(log.toEntity(deviceIdAndPackageName))
    }

    override fun observe(id: Long): Flow<TimelineLogDomainModel?> = timelineLogDao.observe(id)
        .map { it?.toDomain() }
        .distinctUntilChanged()
}
