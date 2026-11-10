package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository

class SetTimelineGraphSingleUseCase(
    private val timelinePreferencesRepository: TimelinePreferencesRepository,
) {

    suspend operator fun invoke(graphSingle: Boolean) {
        timelinePreferencesRepository.setGraphSingle(graphSingle)
    }
}
