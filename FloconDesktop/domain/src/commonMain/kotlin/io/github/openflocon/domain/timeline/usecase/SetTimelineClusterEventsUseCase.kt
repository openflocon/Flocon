package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository

class SetTimelineClusterEventsUseCase(
    private val timelinePreferencesRepository: TimelinePreferencesRepository,
) {

    suspend operator fun invoke(clusterEvents: Boolean) {
        timelinePreferencesRepository.setClusterEvents(clusterEvents)
    }
}
