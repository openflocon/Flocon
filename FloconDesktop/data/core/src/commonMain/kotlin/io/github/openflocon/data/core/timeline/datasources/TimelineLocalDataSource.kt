package io.github.openflocon.data.core.timeline.datasources

import androidx.paging.PagingData
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineActivityDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSummaryDomainModel
import kotlinx.coroutines.flow.Flow

interface TimelineLocalDataSource {
    fun observeActivity(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: TimelineFilterDomainModel,
        bucketCount: Int,
    ): Flow<TimelineActivityDomainModel>
    fun observeSummary(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: TimelineFilterDomainModel,
    ): Flow<TimelineSummaryDomainModel>

    fun observeTimeline(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: TimelineFilterDomainModel,
    ): Flow<PagingData<TimelineEventDomainModel>>
}
