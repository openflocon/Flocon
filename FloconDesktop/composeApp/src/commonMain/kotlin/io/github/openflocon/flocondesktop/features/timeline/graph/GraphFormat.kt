package io.github.openflocon.flocondesktop.features.timeline.graph

import kotlin.math.abs
import kotlin.math.ln

/**
 * "999 ms", "1.00 s", "1 min 05 s".
 */
internal fun formatGraphDuration(ms: Long): String = when {
    ms < 1_000L -> "$ms ms"

    ms < 60_000L -> formatSeconds(ms, decimals = 2) + " s"

    else -> {
        val seconds = ms / 1_000L
        "${seconds / 60} min ${(seconds % 60).toString().padStart(2, '0')} s"
    }
}

// "+1.32 s"
internal fun formatGraphStart(ms: Long): String = "+" + formatGraphDuration(ms)

// "1.2" for 1200 ms with 1 decimal, rounded down: a tick is never later than it says
internal fun formatSeconds(ms: Long, decimals: Int): String {
    val msPerUnit = when (decimals) {
        0 -> 1_000L
        1 -> 100L
        2 -> 10L
        else -> 1L
    }
    val units = ms / msPerUnit
    if (decimals == 0) return units.toString()
    val unitsPerSecond = 1_000L / msPerUnit
    return "${units / unitsPerSecond}.${(units % unitsPerSecond).toString().padStart(decimals, '0')}"
}

/**
 * The step of the axis: 1, 2 or 5 times a power of ten, in ms, to get about [targetPx] between two ticks.
 */
internal fun axisStepMs(pxPerMs: Double, targetPx: Double = 100.0): Long {
    if (pxPerMs <= 0.0) return 1L
    val raw = targetPx / pxPerMs
    var magnitude = 1L
    while (magnitude * 10 <= raw && magnitude < 1_000_000_000L) magnitude *= 10
    // the candidate closest to the target, in the ratio: 1, 2, 5 and the next power of ten
    return listOf(1L, 2L, 5L, 10L).map { it * magnitude }.minBy { abs(ln(it / raw)) }
}

// "0", "300 ms", "0.3 s"
internal fun formatAxisLabel(ms: Long, stepMs: Long): String = when {
    ms == 0L -> "0"

    stepMs >= 1_000L && stepMs % 1_000L == 0L -> "${ms / 1_000L} s"

    ms < 1_000L -> "$ms ms"

    else -> formatSeconds(
        ms,
        decimals = if (stepMs >= 100L) {
            1
        } else if (stepMs >= 10L) {
            2
        } else {
            3
        }
    ) + " s"
}

/**
 * "15 of 20 events - 3.00 s session - 2 failed - slowest: /v1/feed (1.21 s)"
 */
internal data class GraphSummaryText(
    val visible: Int,
    val total: Int,
    val sessionDuration: String,
    val failed: Int,
    val slowestName: String?,
    val slowestDuration: String?,
)

internal fun GraphUiModel.toSummaryText(total: Int = eventCount) = GraphSummaryText(
    visible = eventCount,
    total = total,
    sessionDuration = formatGraphDuration(sessionDurationMs),
    failed = errorCount,
    slowestName = slowest?.name,
    slowestDuration = slowest?.durationMs?.let(::formatGraphDuration),
)

// the ticks are about this far apart
internal const val AXIS_TICK_PX = 50.0

// one tick out of this many is longer and has a line in the lanes
internal const val LONG_TICK_EVERY = 2

// a duration under one tick out of this many: about 200 px apart, some on any screen
internal const val LABEL_EVERY_TICKS = 4

// a time of day above one tick out of this many, so one label out of two
internal const val CLOCK_EVERY_TICKS = 8

// "12:00:05.123" is "12:00:05" when the ticks are a second apart or more
internal fun formatClockLabel(timeOfDay: String, stepMs: Long): String = if (stepMs >= 1_000L) timeOfDay.substringBefore('.') else timeOfDay
