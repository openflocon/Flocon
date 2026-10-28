package io.github.openflocon.flocondesktop.features.timeline.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.timeline.models.TimelineSource

@Immutable
internal data class TimelineSummaryUiModel(
    // events of each source, for the text of the filter: shown on the filters, selected or not
    val sourceCounts: Map<TimelineSource, Int>,
    // the counters of the header only count what the selected filters display
    val eventCount: Int,
    val sessionCount: Int,
    val httpErrorCount: Int,
    val crashCount: Int,
    // first and last event displayed, ex: "19:28:42 - 19:31:07"
    val range: String?,
    // number of displayed events of each session, by start of the session
    val sessionEventCounts: Map<Long, Int>,
) {
    companion object {
        val Empty = TimelineSummaryUiModel(
            sourceCounts = emptyMap(),
            eventCount = 0,
            sessionCount = 0,
            httpErrorCount = 0,
            crashCount = 0,
            range = null,
            sessionEventCounts = emptyMap(),
        )
    }
}

internal fun previewTimelineSummaryUiModel() = TimelineSummaryUiModel(
    sourceCounts = mapOf(
        TimelineSource.Network to 8,
        TimelineSource.Database to 1,
        TimelineSource.Analytics to 1,
        TimelineSource.Accessibility to 1,
        TimelineSource.Crash to 1,
        TimelineSource.Table to 1,
        TimelineSource.Deeplink to 1,
        TimelineSource.Adb to 1,
    ),
    eventCount = 15,
    sessionCount = 1,
    httpErrorCount = 2,
    crashCount = 1,
    range = "12:00:00 - 12:03:41",
    sessionEventCounts = mapOf(0L to 15),
)
