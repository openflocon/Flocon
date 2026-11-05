package io.github.openflocon.data.core.timelinelog.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timelinelog.models.TimelineLogDomainModel
import kotlinx.coroutines.flow.Flow

interface TimelineLogLocalDataSource {

    /**
     * Stores the log, and drops the oldest ones beyond the retention cap
     */
    suspend fun insert(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        log: TimelineLogDomainModel,
    )

    fun observe(id: Long): Flow<TimelineLogDomainModel?>
}
