package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository

class SetTimelineSelectedSourcesUseCase(
    private val timelinePreferencesRepository: TimelinePreferencesRepository,
) {

    suspend operator fun invoke(sources: Set<TimelineSource>) {
        timelinePreferencesRepository.setSelectedSources(sources)
    }
}
