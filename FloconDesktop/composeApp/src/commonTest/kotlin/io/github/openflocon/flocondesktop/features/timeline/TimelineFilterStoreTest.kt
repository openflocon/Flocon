package io.github.openflocon.flocondesktop.features.timeline

import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineRuleDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSettingsDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository
import io.github.openflocon.domain.timeline.repository.TimelineUserFilterProvider
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSettingsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class TimelineFilterStoreTest {

    private class FakePreferences(selected: Set<TimelineSource>) : TimelinePreferencesRepository {
        override val settings: Flow<TimelineSettingsDomainModel> = flowOf(TimelineSettingsDomainModel(selectedSources = selected))

        override suspend fun setAutoScroll(autoScroll: Boolean) = error("not used")
        override suspend fun setInvertList(invertList: Boolean) = error("not used")
        override suspend fun setPinPanel(pinPanel: Boolean) = error("not used")
        override suspend fun setGraph(graph: Boolean) = error("not used")
        override suspend fun setSelectedSources(sources: Set<TimelineSource>) = error("not used")
        override suspend fun setScreenshots(screenshots: Boolean) = error("not used")
        override suspend fun setScreenshotIntervalSeconds(seconds: Int) = error("not used")
    }

    private val selected = setOf(TimelineSource.Network, TimelineSource.Crash)
    private val store = TimelineFilterStore(ObserveTimelineSettingsUseCase(FakePreferences(selected)))
    private val provider: TimelineUserFilterProvider = store

    @Test
    fun `the current filter has what the user typed and ticked, even if nobody observes the filter`() = runBlocking {
        store.setText("checkout")
        store.toggleFacet(TimelineFacet.NetworkMethod, "GET")
        store.setCurrentSessionOnly(true)
        store.addStatusRule(TimelineRuleDomainModel(value = "500", exclude = false))

        val filter = provider.current()

        // no wait for the debounce of the text
        assertEquals("checkout", filter.text)
        assertEquals(selected, filter.sources)
        assertEquals(mapOf(TimelineFacet.NetworkMethod to setOf("GET")), filter.facets)
        assertEquals(true, filter.currentSessionOnly)
        assertEquals(listOf(TimelineRuleDomainModel(value = "500", exclude = false)), filter.networkStatusRules)
    }

    @Test
    fun `reading the current filter changes nothing`() = runBlocking {
        store.setText("checkout")

        provider.current()

        assertEquals("checkout", store.text.value)
        assertEquals(emptyMap(), store.facets.value)
    }
}
