package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository

class SetTimelineMergePluginsUseCase(
    private val timelinePreferencesRepository: TimelinePreferencesRepository,
) {

    suspend operator fun invoke(mergePlugins: Boolean) {
        timelinePreferencesRepository.setMergePlugins(mergePlugins)
    }
}
