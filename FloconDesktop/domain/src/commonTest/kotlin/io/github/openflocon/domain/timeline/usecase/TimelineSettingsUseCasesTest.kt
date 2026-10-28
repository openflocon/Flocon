package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.timeline.models.TimelineSettingsDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class TimelineSettingsUseCasesTest {

    private class FakeTimelinePreferencesRepository : TimelinePreferencesRepository {
        val state = MutableStateFlow(TimelineSettingsDomainModel())
        override val settings = state

        override suspend fun setAutoScroll(autoScroll: Boolean) {
            state.value = state.value.copy(autoScroll = autoScroll)
        }

        override suspend fun setInvertList(invertList: Boolean) {
            state.value = state.value.copy(invertList = invertList)
        }

        override suspend fun setPinPanel(pinPanel: Boolean) {
            state.value = state.value.copy(pinPanel = pinPanel)
        }

        override suspend fun setSelectedSources(sources: Set<TimelineSource>) {
            state.value = state.value.copy(selectedSources = sources)
        }
    }

    private val repository = FakeTimelinePreferencesRepository()

    @Test
    fun `both options are off by default`() = runBlocking {
        assertEquals(
            TimelineSettingsDomainModel(autoScroll = false, invertList = false, pinPanel = false),
            ObserveTimelineSettingsUseCase(repository)().first(),
        )
    }

    @Test
    fun `the auto scroll is set without changing the direction`() = runBlocking {
        SetTimelineAutoScrollUseCase(repository)(true)

        assertEquals(
            TimelineSettingsDomainModel(autoScroll = true, invertList = false),
            ObserveTimelineSettingsUseCase(repository)().first(),
        )
    }

    @Test
    fun `the direction is set without changing the auto scroll`() = runBlocking {
        SetTimelineAutoScrollUseCase(repository)(true)
        SetTimelineInvertListUseCase(repository)(true)

        assertEquals(
            TimelineSettingsDomainModel(autoScroll = true, invertList = true),
            ObserveTimelineSettingsUseCase(repository)().first(),
        )
    }

    @Test
    fun `the panel is pinned without changing the other options`() = runBlocking {
        SetTimelineInvertListUseCase(repository)(true)
        SetTimelinePinPanelUseCase(repository)(true)

        assertEquals(
            TimelineSettingsDomainModel(autoScroll = false, invertList = true, pinPanel = true),
            ObserveTimelineSettingsUseCase(repository)().first(),
        )
    }

    @Test
    fun `every source is selected by default`() = runBlocking {
        assertEquals(
            TimelineSource.entries.toSet(),
            ObserveTimelineSettingsUseCase(repository)().first().selectedSources,
        )
    }

    @Test
    fun `a source can be deselected then selected again, without changing the other options`() = runBlocking {
        SetTimelineInvertListUseCase(repository)(true)
        val select = SetTimelineSelectedSourcesUseCase(repository)

        select(TimelineSource.entries.toSet() - TimelineSource.Database)
        val deselected = ObserveTimelineSettingsUseCase(repository)().first()
        assertEquals(TimelineSource.entries.toSet() - TimelineSource.Database, deselected.selectedSources)
        assertEquals(true, deselected.invertList)

        select(TimelineSource.entries.toSet())
        assertEquals(
            TimelineSource.entries.toSet(),
            ObserveTimelineSettingsUseCase(repository)().first().selectedSources,
        )
    }
}
