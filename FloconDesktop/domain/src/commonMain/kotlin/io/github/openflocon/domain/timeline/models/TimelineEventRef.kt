package io.github.openflocon.domain.timeline.models

/** Points at one event of the timeline: its source and its id in the table of that source */
data class TimelineEventRef(
    val source: TimelineSource,
    val id: String,
)
