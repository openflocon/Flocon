package io.github.openflocon.domain.timeline.usecase

import androidx.paging.PagingData
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventRef
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineHiddenReason
import io.github.openflocon.domain.timeline.models.TimelineRuleDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.models.TimelineSummaryDomainModel
import io.github.openflocon.domain.timeline.repository.TimelineRepository
import io.github.openflocon.domain.timeline.repository.TimelineUserFilterProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetTimelineEventHiddenReasonsUseCaseTest {

    private val target = DeviceIdAndPackageNameDomainModel(deviceId = "pixel", packageName = "io.flocon.sample", appInstance = 1L)
    private val crash = TimelineEventDomainModel(
        id = "7",
        source = TimelineSource.Crash,
        timestamp = 100L,
        title = "IllegalStateException",
        subtitle = null,
        appInstance = null,
    )
    private val ref = TimelineEventRef(TimelineSource.Crash, "7")

    // lets the event through like the real query would, for the few filters this test plays with
    private class FakeRepository(private val event: TimelineEventDomainModel) : TimelineRepository {
        val askedFilters = mutableListOf<TimelineFilterDomainModel>()

        override fun observeTimelineEvents(
            deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
            filter: TimelineFilterDomainModel,
        ): Flow<List<TimelineEventDomainModel>> {
            askedFilters += filter
            val visible = event.source in filter.sources &&
                (filter.text.isNullOrBlank() || event.title.contains(filter.text!!)) &&
                // the event belongs to an older session
                !filter.currentSessionOnly &&
                filter.facets.isEmpty() &&
                filter.networkStatusRules.isEmpty()
            return flowOf(if (visible) listOf(event) else emptyList())
        }

        override fun observeSummary(
            deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
            filter: TimelineFilterDomainModel,
        ): Flow<TimelineSummaryDomainModel> = error("not used")

        override fun observeTimeline(
            deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
            filter: TimelineFilterDomainModel,
        ): Flow<PagingData<TimelineEventDomainModel>> = error("not used")
    }

    private fun reasons(userFilter: TimelineFilterDomainModel, repository: FakeRepository = FakeRepository(crash)): List<TimelineHiddenReason> = runBlocking {
        GetTimelineEventHiddenReasonsUseCase(
            userFilter = object : TimelineUserFilterProvider {
                override suspend fun current() = userFilter
            },
            getTimelineEventsUseCase = GetTimelineEventsUseCase(repository),
        )(target, ref)
    }

    @Test
    fun `a visible event gives no reason`() {
        assertEquals(emptyList(), reasons(TimelineFilterDomainModel()))
    }

    @Test
    fun `an event hidden only by an unchecked source gives Source`() {
        val filter = TimelineFilterDomainModel(sources = TimelineSource.entries.toSet() - TimelineSource.Crash)

        assertEquals(listOf(TimelineHiddenReason.Source), reasons(filter))
    }

    @Test
    fun `an event hidden only by the text search gives Text`() {
        assertEquals(listOf(TimelineHiddenReason.Text), reasons(TimelineFilterDomainModel(text = "checkout")))
    }

    @Test
    fun `an event hidden only by current session only gives CurrentSessionOnly`() {
        assertEquals(listOf(TimelineHiddenReason.CurrentSessionOnly), reasons(TimelineFilterDomainModel(currentSessionOnly = true)))
    }

    @Test
    fun `an event hidden only by the facets gives Facets`() {
        val filter = TimelineFilterDomainModel(facets = mapOf(TimelineFacet.CrashException to setOf("IllegalStateException")))

        assertEquals(listOf(TimelineHiddenReason.Facets), reasons(filter))
    }

    @Test
    fun `an event hidden only by the network rules gives NetworkRules`() {
        val filter = TimelineFilterDomainModel(networkStatusRules = listOf(TimelineRuleDomainModel(value = "500", exclude = false)))

        assertEquals(listOf(TimelineHiddenReason.NetworkRules), reasons(filter))
    }

    @Test
    fun `an event hidden by two filters gives Combination`() {
        val filter = TimelineFilterDomainModel(sources = TimelineSource.entries.toSet() - TimelineSource.Crash, text = "checkout")

        assertEquals(listOf(TimelineHiddenReason.Combination), reasons(filter))
    }

    @Test
    fun `only the event asked is read, with no time bound, and the filter of the user is never changed`() {
        val repository = FakeRepository(crash)
        val filter = TimelineFilterDomainModel(text = "checkout", fromMs = 1L, toMs = 2L)

        reasons(filter, repository)

        assertTrue(repository.askedFilters.all { it.events == setOf(ref) && it.fromMs == null && it.toMs == null })
        assertEquals("checkout", filter.text)
    }
}
