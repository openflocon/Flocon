package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSummaryDomainModel
import io.github.openflocon.domain.timeline.repository.TimelineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveTimelineSummaryUseCase(
    private val repository: TimelineRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(
        filter: TimelineFilterDomainModel,
    ): Flow<TimelineSummaryDomainModel> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { current ->
            if (current == null) {
                flowOf(TimelineSummaryDomainModel.Empty)
            } else {
                repository.observeSummary(
                    deviceIdAndPackageName = current,
                    filter = filter,
                )
            }
        }
}
