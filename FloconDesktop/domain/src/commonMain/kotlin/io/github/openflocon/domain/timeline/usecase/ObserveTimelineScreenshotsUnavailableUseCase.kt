package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotRecorder
import kotlinx.coroutines.flow.StateFlow

class ObserveTimelineScreenshotsUnavailableUseCase(
    private val timelineScreenshotRecorder: TimelineScreenshotRecorder,
) {

    operator fun invoke(): StateFlow<Boolean> = timelineScreenshotRecorder.isUnavailable
}
