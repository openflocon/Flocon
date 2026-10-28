package io.github.openflocon.flocondesktop.core.data.settings

import io.github.openflocon.domain.timeline.models.TimelineSettingsDomainModel
import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class TimelinePreferencesRepositoryImpl(
    private val localSettingsDataSource: SettingsDataSource,
) : TimelinePreferencesRepository {

    override val settings: Flow<TimelineSettingsDomainModel> = combine(
        localSettingsDataSource.timelineAutoScroll,
        localSettingsDataSource.timelineInvertList,
        localSettingsDataSource.timelinePinPanel,
    ) { autoScroll, invertList, pinPanel ->
        TimelineSettingsDomainModel(autoScroll = autoScroll, invertList = invertList, pinPanel = pinPanel)
    }

    override suspend fun setAutoScroll(autoScroll: Boolean) {
        localSettingsDataSource.setTimelineAutoScroll(autoScroll)
    }

    override suspend fun setInvertList(invertList: Boolean) {
        localSettingsDataSource.setTimelineInvertList(invertList)
    }

    override suspend fun setPinPanel(pinPanel: Boolean) {
        localSettingsDataSource.setTimelinePinPanel(pinPanel)
    }
}
