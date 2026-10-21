package io.github.openflocon.flocondesktop.features.timeline.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.timeline.models.TimelineSource

// when the displayed events happened, to draw the activity strip
@Immutable
internal data class TimelineActivityUiModel(
    val bucketCount: Int,
    // one row per displayed source, in the order of the filters
    val rows: List<TimelineActivityRowUiModel>,
    // the slices where an app starts, if the sessions are displayed
    val sessionBuckets: Set<Int>,
) {
    val isEmpty: Boolean get() = rows.all { it.buckets.isEmpty() }

    companion object {
        val Empty = TimelineActivityUiModel(bucketCount = 1, rows = emptyList(), sessionBuckets = emptySet())
    }
}

@Immutable
internal data class TimelineActivityRowUiModel(
    val source: TimelineSource,
    // slice -> number of events
    val buckets: Map<Int, Int>,
)
