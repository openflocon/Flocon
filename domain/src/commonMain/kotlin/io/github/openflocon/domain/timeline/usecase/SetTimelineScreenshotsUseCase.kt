package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository

class SetTimelineScreenshotsUseCase(
    private val timelinePreferencesRepository: TimelinePreferencesRepository,
) {

    suspend operator fun invoke(enabled: Boolean) {
        timelinePreferencesRepository.setScreenshots(enabled)
    }
}
