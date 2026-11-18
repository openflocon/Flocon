package io.github.openflocon.domain.timeline.repository

import androidx.paging.PagingData
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSummaryDomainModel
import kotlinx.coroutines.flow.Flow

interface TimelineRepository {
    // the counters follow the text of the filter, whatever the selected sources
    fun observeSummary(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: TimelineFilterDomainModel,
    ): Flow<TimelineSummaryDomainModel>

    // the events of the filter in one list, newest first, capped: the graph draws a whole session at once
    fun observeTimelineEvents(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: TimelineFilterDomainModel,
    ): Flow<List<TimelineEventDomainModel>>

    // newest first
    fun observeTimeline(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: TimelineFilterDomainModel,
    ): Flow<PagingData<TimelineEventDomainModel>>
}
