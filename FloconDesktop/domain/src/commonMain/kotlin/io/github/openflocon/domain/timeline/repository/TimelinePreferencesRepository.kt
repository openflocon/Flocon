package io.github.openflocon.domain.timeline.repository

import io.github.openflocon.domain.timeline.models.TimelineSettingsDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import kotlinx.coroutines.flow.Flow

interface TimelinePreferencesRepository {
    val settings: Flow<TimelineSettingsDomainModel>

    suspend fun setAutoScroll(autoScroll: Boolean)

    suspend fun setInvertList(invertList: Boolean)

    suspend fun setPinPanel(pinPanel: Boolean)

    suspend fun setSelectedSources(sources: Set<TimelineSource>)
}
