package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository

class SetTimelinePinPanelUseCase(
    private val timelinePreferencesRepository: TimelinePreferencesRepository,
) {

    suspend operator fun invoke(pinPanel: Boolean) {
        timelinePreferencesRepository.setPinPanel(pinPanel)
    }
}
