package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.timeline.models.TimelineScreenshotIntervalsSeconds
import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository

class SetTimelineScreenshotIntervalUseCase(
    private val timelinePreferencesRepository: TimelinePreferencesRepository,
) {

    suspend operator fun invoke(seconds: Int) {
        if (seconds in TimelineScreenshotIntervalsSeconds) {
            timelinePreferencesRepository.setScreenshotIntervalSeconds(seconds)
        }
    }
}
