package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository

class SetTimelineAutoScrollUseCase(
    private val timelinePreferencesRepository: TimelinePreferencesRepository,
) {

    suspend operator fun invoke(autoScroll: Boolean) {
        timelinePreferencesRepository.setAutoScroll(autoScroll)
    }
}
