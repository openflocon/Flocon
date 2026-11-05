package io.github.openflocon.flocondesktop.features.timeline

import io.github.openflocon.domain.timeline.models.TIMELINE_DATABASE_STATEMENT_ROOM
import io.github.openflocon.domain.timeline.models.TIMELINE_DEFAULT_HIDDEN_FACETS
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
import kotlin.test.assertTrue

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
        assertEquals(TIMELINE_DEFAULT_HIDDEN_FACETS + (TimelineFacet.NetworkMethod to setOf("GET")), filter.facets)
        assertEquals(true, filter.currentSessionOnly)
        assertEquals(listOf(TimelineRuleDomainModel(value = "500", exclude = false)), filter.networkStatusRules)
    }

    @Test
    fun `the current filter has the path and database rules too`() = runBlocking {
        store.addPathRule(TimelineRuleDomainModel(value = "/raw", exclude = true))
        store.addDatabaseRule(TimelineRuleDomainModel(value = "users", exclude = false))
        store.setHideDatabaseTransactions(true)

        val filter = provider.current()

        assertEquals(listOf(TimelineRuleDomainModel(value = "/raw", exclude = true)), filter.networkPathRules)
        assertEquals(listOf(TimelineRuleDomainModel(value = "users", exclude = false)), filter.databaseRules)
        assertEquals(true, filter.hideDatabaseTransactions)
    }

    @Test
    fun `reading the current filter changes nothing`() = runBlocking {
        store.setText("checkout")

        provider.current()

        assertEquals("checkout", store.text.value)
        assertEquals(TIMELINE_DEFAULT_HIDDEN_FACETS, store.facets.value)
    }

    @Test
    fun `what Room runs for itself is hidden by default, and can be shown again`() = runBlocking {
        assertEquals(setOf(TIMELINE_DATABASE_STATEMENT_ROOM), provider.current().facets[TimelineFacet.DatabaseStatement])

        store.toggleFacet(TimelineFacet.DatabaseStatement, TIMELINE_DATABASE_STATEMENT_ROOM)

        assertEquals(null, provider.current().facets[TimelineFacet.DatabaseStatement])
    }

    @Test
    fun `clearing the filter shows what Room runs for itself too`() = runBlocking {
        store.clear()

        assertEquals(emptyMap(), provider.current().facets)
    }

    @Test
    fun `hiding a value twice is the same as once`() {
        store.hideFacetValue(TimelineFacet.DatabaseName, "app.db")
        store.hideFacetValue(TimelineFacet.DatabaseName, "app.db")
        store.hideFacetValue(TimelineFacet.DatabaseName, "other.db")

        assertEquals(TIMELINE_DEFAULT_HIDDEN_FACETS + (TimelineFacet.DatabaseName to setOf("app.db", "other.db")), store.facets.value)
    }

    @Test
    fun `hiding a value of the network turns its filters back on`() {
        store.setNetworkEnabled(false)

        store.hideFacetValue(TimelineFacet.NetworkMethod, "GET")

        assertTrue(store.network.value.enabled)
    }

    @Test
    fun `hiding a path adds an exclude rule once and turns the network filters back on`() {
        store.setNetworkEnabled(false)

        store.hidePath("/raw")
        store.hidePath("/raw")

        assertEquals(listOf(TimelineRuleDomainModel("/raw", exclude = true)), store.network.value.pathRules)
        assertEquals(1, store.network.value.ruleCount)
        assertTrue(store.network.value.enabled)

        store.clear()
        assertEquals(emptyList(), store.network.value.pathRules)
    }

    @Test
    fun `a source can be asked to be shown in the panel, once`() {
        store.focusSource(TimelineSource.Database)
        assertEquals(TimelineSource.Database, store.focusedSource.value)

        store.focusSource(null)
        assertEquals(null, store.focusedSource.value)
    }

    @Test
    fun `hiding a value of another source leaves the network card as it is`() {
        store.setNetworkEnabled(false)

        store.hideFacetValue(TimelineFacet.CrashException, "java.lang.Error")

        assertEquals(false, store.network.value.enabled)
    }

    @Test
    fun `adding a log tag rule again changes its side and activates it`() = runBlocking {
        store.addLogTagRule("billing", exclude = false)
        store.setLogTagRuleActive("billing", false)
        store.addLogTagRule("billing", exclude = true)

        assertEquals(listOf(TimelineLogTagRule("billing", exclude = true, active = true)), store.logs.value.tagRules)
    }

    @Test
    fun `an inactive log tag rule is kept but not in the domain filter`() = runBlocking {
        store.addLogTagRule("billing", exclude = false)
        store.addLogTagRule("auth", exclude = true)
        store.setLogTagRuleActive("billing", false)
        store.setLogTaggedEnabled(false)

        val filter = provider.current()

        assertEquals(listOf(TimelineRuleDomainModel("auth", exclude = true)), filter.logTagRules)
        assertEquals(false, filter.logTaggedEnabled)
        assertEquals(2, store.logs.value.tagRules.size)

        store.removeLogTagRule("auth")
        assertEquals(listOf("billing"), store.logs.value.tagRules.map { it.value })
    }

    @Test
    fun `clearing resets the logs and the database rules`() = runBlocking {
        store.addLogTagRule("billing", exclude = false)
        store.setLogTaggedEnabled(false)
        store.addDatabaseRule(TimelineRuleDomainModel(value = "users", exclude = false))

        store.clear()

        assertEquals(TimelineLogRules(), store.logs.value)
        assertEquals(TimelineDatabaseRules(), store.database.value)
        assertEquals(true, provider.current().logTaggedEnabled)
    }
}
