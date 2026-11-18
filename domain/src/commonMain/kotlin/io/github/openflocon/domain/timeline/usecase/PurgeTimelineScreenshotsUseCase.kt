package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotRepository

class PurgeTimelineScreenshotsUseCase(
    private val timelineScreenshotRepository: TimelineScreenshotRepository,
) {

    suspend operator fun invoke() {
        timelineScreenshotRepository.purgeAll()
    }
}
