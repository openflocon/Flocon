package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventRef
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineHiddenReason
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.repository.TimelineUserFilterProvider

/**
 * Why an event is not displayed by the filters of the user: empty when it is displayed.
 * The filters are only read, each one is reset alone on a copy to see which one hides the event.
 */
class GetTimelineEventHiddenReasonsUseCase(
    private val userFilter: TimelineUserFilterProvider,
    private val getTimelineEventsUseCase: GetTimelineEventsUseCase,
) {
    suspend operator fun invoke(target: DeviceIdAndPackageNameDomainModel, event: TimelineEventRef): List<TimelineHiddenReason> {
        val filter = userFilter.current()

        suspend fun displayedWith(filter: TimelineFilterDomainModel): Boolean = getTimelineEventsUseCase(
            target,
            filter.copy(events = setOf(event), fromMs = null, toMs = null),
        ).isNotEmpty()

        if (displayedWith(filter)) return emptyList()

        val resets = mapOf(
            TimelineHiddenReason.Source to filter.copy(sources = TimelineSource.entries.toSet()),
            TimelineHiddenReason.Text to filter.copy(text = null),
            TimelineHiddenReason.Facets to filter.copy(facets = emptyMap(), logTaggedEnabled = true, logTagRules = emptyList()),
            TimelineHiddenReason.NetworkRules to filter.copy(
                networkStatusRules = emptyList(),
                networkHostRules = emptyList(),
                networkDuration = null,
            ),
            TimelineHiddenReason.CurrentSessionOnly to filter.copy(currentSessionOnly = false),
        )
        val alone = resets.filter { (_, reset) -> displayedWith(reset) }.keys.toList()
        return alone.ifEmpty { listOf(TimelineHiddenReason.Combination) }
    }
}
