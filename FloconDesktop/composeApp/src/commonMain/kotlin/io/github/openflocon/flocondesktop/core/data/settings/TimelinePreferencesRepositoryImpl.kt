package io.github.openflocon.flocondesktop.core.data.settings

import io.github.openflocon.domain.timeline.models.TimelineSettingsDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import io.github.openflocon.flocondesktop.core.data.settings.models.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class TimelinePreferencesRepositoryImpl(
    private val localSettingsDataSource: SettingsDataSource,
) : TimelinePreferencesRepository {

    override val settings: Flow<TimelineSettingsDomainModel> = localSettingsDataSource.timelineSettings
        .map { it.toDomain() }

    override suspend fun setAutoScroll(autoScroll: Boolean) {
        localSettingsDataSource.updateTimelineSettings { it.copy(autoScroll = autoScroll) }
    }

    override suspend fun setInvertList(invertList: Boolean) {
        localSettingsDataSource.updateTimelineSettings { it.copy(invertList = invertList) }
    }

    override suspend fun setPinPanel(pinPanel: Boolean) {
        localSettingsDataSource.updateTimelineSettings { it.copy(pinPanel = pinPanel) }
    }

    override suspend fun setSelectedSources(sources: Set<TimelineSource>) {
        localSettingsDataSource.updateTimelineSettings { current ->
            current.copy(
                disabledSources = TimelineSource.entries.filter { it !in sources }.map { it.name }.toSet(),
            )
        }
    }
}
