package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSummaryDomainModel
import io.github.openflocon.domain.timeline.repository.TimelineRepository
import kotlinx.coroutines.flow.first

/** The counters and time ranges of one device and app at this moment, whatever the device selected in the app */
class GetTimelineSummaryUseCase(
    private val repository: TimelineRepository,
) {
    suspend operator fun invoke(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: TimelineFilterDomainModel,
    ): TimelineSummaryDomainModel = repository.observeSummary(deviceIdAndPackageName, filter).first()
}
