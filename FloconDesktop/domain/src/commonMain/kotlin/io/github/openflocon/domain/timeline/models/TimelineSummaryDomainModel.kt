package io.github.openflocon.domain.timeline.models

import io.github.openflocon.domain.device.models.AppInstance

// what the timeline counts, without loading the events
data class TimelineSummaryDomainModel(
    val sources: Map<TimelineSource, TimelineSourceSummaryDomainModel>,
    // number of displayed events of each session
    val sessionEventCounts: Map<AppInstance, Int>,
) {
    companion object {
        val Empty = TimelineSummaryDomainModel(sources = emptyMap(), sessionEventCounts = emptyMap())
    }
}

data class TimelineSourceSummaryDomainModel(
    val count: Int,
    val httpErrorCount: Int, // 4xx and 5xx
    val firstTimestamp: Long?,
    val lastTimestamp: Long?,
)
