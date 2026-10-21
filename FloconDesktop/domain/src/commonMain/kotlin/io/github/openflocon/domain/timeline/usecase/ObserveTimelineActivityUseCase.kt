package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.timeline.models.TimelineActivityDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.repository.TimelineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveTimelineActivityUseCase(
    private val repository: TimelineRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(
        filter: TimelineFilterDomainModel,
        bucketCount: Int,
    ): Flow<TimelineActivityDomainModel> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { current ->
            if (current == null) {
                flowOf(TimelineActivityDomainModel.empty(bucketCount))
            } else {
                repository.observeActivity(
                    deviceIdAndPackageName = current,
                    filter = filter,
                    bucketCount = bucketCount,
                )
            }
        }
}
