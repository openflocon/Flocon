package io.github.openflocon.domain.timeline.usecase

import androidx.paging.PagingData
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.models.TimelineSummaryDomainModel
import io.github.openflocon.domain.timeline.repository.TimelineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTimelineEventsUseCaseTest {

    private class FakeRepository(private val events: List<TimelineEventDomainModel>) : TimelineRepository {
        var askedTarget: DeviceIdAndPackageNameDomainModel? = null
        var askedFilter: TimelineFilterDomainModel? = null

        override fun observeSummary(
            deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
            filter: TimelineFilterDomainModel,
        ): Flow<TimelineSummaryDomainModel> = error("not used")

        override fun observeTimelineEvents(
            deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
            filter: TimelineFilterDomainModel,
        ): Flow<List<TimelineEventDomainModel>> {
            askedTarget = deviceIdAndPackageName
            askedFilter = filter
            return flowOf(events)
        }

        override fun observeTimeline(
            deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
            filter: TimelineFilterDomainModel,
        ): Flow<PagingData<TimelineEventDomainModel>> = error("not used")
    }

    private val event = TimelineEventDomainModel(
        id = "1",
        source = TimelineSource.Crash,
        timestamp = 10L,
        title = "IllegalStateException",
        subtitle = null,
        appInstance = null,
    )
    private val target = DeviceIdAndPackageNameDomainModel(deviceId = "pixel", packageName = "io.flocon.sample", appInstance = 1L)

    @Test
    fun `the events of the given device and filter are read once`() = runBlocking {
        val repository = FakeRepository(listOf(event))
        val filter = TimelineFilterDomainModel(fromMs = 1L, toMs = 20L)

        val events = GetTimelineEventsUseCase(repository)(target, filter)

        assertEquals(listOf(event), events)
        assertEquals(target, repository.askedTarget)
        assertEquals(filter, repository.askedFilter)
    }
}
