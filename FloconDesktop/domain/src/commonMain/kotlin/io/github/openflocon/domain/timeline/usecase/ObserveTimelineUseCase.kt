package io.github.openflocon.domain.timeline.usecase

import androidx.paging.PagingData
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.repository.TimelineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveTimelineUseCase(
    private val repository: TimelineRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(
        filter: TimelineFilterDomainModel,
    ): Flow<PagingData<TimelineEventDomainModel>> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { current ->
            if (current == null) {
                flowOf(PagingData.empty())
            } else {
                repository.observeTimeline(
                    deviceIdAndPackageName = current,
                    filter = filter,
                )
            }
        }
}
