package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.repository.TimelineRepository
import kotlinx.coroutines.flow.first

/** The events of one device and app at this moment, whatever the device and the filters selected in the app */
class GetTimelineEventsUseCase(
    private val repository: TimelineRepository,
) {
    suspend operator fun invoke(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: TimelineFilterDomainModel,
    ): List<TimelineEventDomainModel> = repository.observeTimelineEvents(deviceIdAndPackageName, filter).first()
}
