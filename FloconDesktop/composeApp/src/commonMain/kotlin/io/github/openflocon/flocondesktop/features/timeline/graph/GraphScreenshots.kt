package io.github.openflocon.flocondesktop.features.timeline.graph

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotDomainModel

// a screenshot of the strip of the graph, [timeMs] since the start of the session
@Immutable
internal data class GraphScreenshotUiModel(
    val timeMs: Long,
    val filePath: String,
)

// the screenshots taken during a session: from its start (included) to the start of the next one (excluded), oldest first
internal fun screenshotsOfSession(
    shots: List<TimelineScreenshotDomainModel>,
    sessionStart: Long,
    nextSessionStart: Long?,
): List<GraphScreenshotUiModel> = shots
    .filter { it.timestamp >= sessionStart && (nextSessionStart == null || it.timestamp < nextSessionStart) }
    .sortedBy { it.timestamp }
    .map { GraphScreenshotUiModel(timeMs = it.timestamp - sessionStart, filePath = it.filePath) }

// the first one, then each one at least [minGapPx] after the last one kept: the thumbnails of the strip never overlap
internal fun thinScreenshots(
    shots: List<GraphScreenshotUiModel>,
    viewport: GraphViewport,
    minGapPx: Float,
): List<GraphScreenshotUiModel> {
    val kept = ArrayList<GraphScreenshotUiModel>()
    var lastX = Float.NEGATIVE_INFINITY
    shots.forEach { shot ->
        val x = viewport.xOf(shot.timeMs)
        if (kept.isEmpty() || x - lastX >= minGapPx) {
            kept += shot
            lastX = x
        }
    }
    return kept
}

// the latest screenshot at or before [timeMs], none when it is older than [maxGapMs] (the capture was paused)
internal fun screenshotBefore(
    shots: List<GraphScreenshotUiModel>,
    timeMs: Long,
    maxGapMs: Long,
): GraphScreenshotUiModel? = shots
    .lastOrNull { it.timeMs <= timeMs }
    ?.takeIf { timeMs - it.timeMs <= maxGapMs }

// "0:30", "61:01": the time of a screenshot since the start of the session
internal fun formatScreenshotTime(ms: Long): String {
    val totalSeconds = ms / 1_000L
    return "${totalSeconds / 60}:${(totalSeconds % 60).toString().padStart(2, '0')}"
}
