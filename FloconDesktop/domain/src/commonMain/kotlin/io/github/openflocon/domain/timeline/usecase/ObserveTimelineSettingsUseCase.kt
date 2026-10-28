package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.timeline.models.TimelineSettingsDomainModel
import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository
import kotlinx.coroutines.flow.Flow

class ObserveTimelineSettingsUseCase(
    private val timelinePreferencesRepository: TimelinePreferencesRepository,
) {

    operator fun invoke(): Flow<TimelineSettingsDomainModel> = timelinePreferencesRepository.settings
}
