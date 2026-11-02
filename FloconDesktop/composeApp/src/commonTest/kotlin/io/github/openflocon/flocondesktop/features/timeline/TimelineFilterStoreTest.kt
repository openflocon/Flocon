package io.github.openflocon.flocondesktop.features.timeline

import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineSettingsDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSettingsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TimelineFilterStoreTest {

    private val repository = object : TimelinePreferencesRepository {
        override val settings: Flow<TimelineSettingsDomainModel> = flowOf(TimelineSettingsDomainModel())
        override suspend fun setAutoScroll(autoScroll: Boolean) = Unit
        override suspend fun setInvertList(invertList: Boolean) = Unit
        override suspend fun setPinPanel(pinPanel: Boolean) = Unit
        override suspend fun setSelectedSources(sources: Set<TimelineSource>) = Unit
    }

    private val store = TimelineFilterStore(ObserveTimelineSettingsUseCase(repository))

    @Test
    fun `hiding a value twice is the same as once`() {
        store.hideFacetValue(TimelineFacet.DatabaseName, "app.db")
        store.hideFacetValue(TimelineFacet.DatabaseName, "app.db")
        store.hideFacetValue(TimelineFacet.DatabaseName, "other.db")

        assertEquals(mapOf(TimelineFacet.DatabaseName to setOf("app.db", "other.db")), store.facets.value)
    }

    @Test
    fun `hiding a value of the network turns its filters back on`() {
        store.setNetworkEnabled(false)

        store.hideFacetValue(TimelineFacet.NetworkMethod, "GET")

        assertTrue(store.network.value.enabled)
    }

    @Test
    fun `hiding a value of another source leaves the network card as it is`() {
        store.setNetworkEnabled(false)

        store.hideFacetValue(TimelineFacet.CrashException, "java.lang.Error")

        assertEquals(false, store.network.value.enabled)
    }
}
