package io.github.openflocon.domain.timeline.usecase

import androidx.paging.PagingData
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.models.TimelineSourceSummaryDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSummaryDomainModel
import io.github.openflocon.domain.timeline.repository.TimelineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTimelineSummaryUseCaseTest {

    private class FakeRepository(private val summary: TimelineSummaryDomainModel) : TimelineRepository {
        var askedTarget: DeviceIdAndPackageNameDomainModel? = null

        override fun observeSummary(
            deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
            filter: TimelineFilterDomainModel,
        ): Flow<TimelineSummaryDomainModel> {
            askedTarget = deviceIdAndPackageName
            return flowOf(summary)
        }

        override fun observeTimelineEvents(
            deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
            filter: TimelineFilterDomainModel,
        ): Flow<List<TimelineEventDomainModel>> = error("not used")

        override fun observeTimeline(
            deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
            filter: TimelineFilterDomainModel,
        ): Flow<PagingData<TimelineEventDomainModel>> = error("not used")
    }

    @Test
    fun `the summary of the given device is read once`() = runBlocking {
        val summary = TimelineSummaryDomainModel(
            sources = mapOf(TimelineSource.Crash to TimelineSourceSummaryDomainModel(count = 1, httpErrorCount = 0, firstTimestamp = 5L, lastTimestamp = 9L)),
            sessionEventCounts = emptyMap(),
        )
        val repository = FakeRepository(summary)
        val target = DeviceIdAndPackageNameDomainModel(deviceId = "pixel", packageName = "io.flocon.sample", appInstance = 1L)

        val result = GetTimelineSummaryUseCase(repository)(target, TimelineFilterDomainModel())

        assertEquals(summary, result)
        assertEquals(target, repository.askedTarget)
    }
}
