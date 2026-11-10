package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotDomainModel
import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

// the screenshots of the current device and app, oldest first
class ObserveTimelineScreenshotsUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val timelineScreenshotRepository: TimelineScreenshotRepository,
) {

    operator fun invoke(): Flow<List<TimelineScreenshotDomainModel>> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { current ->
            if (current == null) flowOf(emptyList()) else timelineScreenshotRepository.observe(current)
        }
        // the list of another device or app changes too: the timeline does not rebuild for an equal list
        .distinctUntilChanged()
}
