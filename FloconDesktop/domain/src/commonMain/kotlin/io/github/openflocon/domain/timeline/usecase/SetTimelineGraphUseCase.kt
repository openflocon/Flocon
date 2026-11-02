package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository

class SetTimelineGraphUseCase(
    private val timelinePreferencesRepository: TimelinePreferencesRepository,
) {

    suspend operator fun invoke(graph: Boolean) {
        timelinePreferencesRepository.setGraph(graph)
    }
}
