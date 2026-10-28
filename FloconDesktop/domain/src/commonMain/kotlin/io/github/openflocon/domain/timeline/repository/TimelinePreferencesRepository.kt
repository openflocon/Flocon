package io.github.openflocon.domain.timeline.repository

import io.github.openflocon.domain.timeline.models.TimelineSettingsDomainModel
import kotlinx.coroutines.flow.Flow

interface TimelinePreferencesRepository {
    val settings: Flow<TimelineSettingsDomainModel>

    suspend fun setAutoScroll(autoScroll: Boolean)

    suspend fun setInvertList(invertList: Boolean)
}
