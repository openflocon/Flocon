package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.common.time.formatTimestamp
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.models.TimelineSummaryDomainModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel

internal fun TimelineSummaryDomainModel.toUi(selected: Set<TimelineSource>): TimelineSummaryUiModel {
    // the app starts are the sessions, not events
    val displayedEvents = sources
        .filterKeys { it != TimelineSource.AppStart && it in selected }
        .values

    val first = displayedEvents.mapNotNull { it.firstTimestamp }.minOrNull()
    val last = displayedEvents.mapNotNull { it.lastTimestamp }.maxOrNull()

    return TimelineSummaryUiModel(
        sourceCounts = sources.mapValues { it.value.count },
        eventCount = displayedEvents.sumOf { it.count },
        sessionCount = sources[TimelineSource.AppStart]?.count ?: 0,
        httpErrorCount = displayedEvents.sumOf { it.httpErrorCount },
        crashCount = sources[TimelineSource.Crash]?.takeIf { TimelineSource.Crash in selected }?.count ?: 0,
        range = if (first != null && last != null) "${first.toClock()} - ${last.toClock()}" else null,
        sessionEventCounts = sessionEventCounts,
        facetCounts = facetCounts,
    )
}

// hh:mm:ss, the milliseconds are too precise for a range
private fun Long.toClock(): String = formatTimestamp(this).substringBeforeLast('.')
