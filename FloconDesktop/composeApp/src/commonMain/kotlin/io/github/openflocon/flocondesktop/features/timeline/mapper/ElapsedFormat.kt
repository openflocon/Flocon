package io.github.openflocon.flocondesktop.features.timeline.mapper

import kotlin.math.roundToLong

private const val SECOND = 1_000.0
private const val MINUTE = 60_000.0

/**
 * A duration as short as possible: "488 ms", "1.70 s", "2 min 05 s".
 */
internal fun formatElapsed(ms: Double): String = when {
    ms < SECOND -> "${ms.roundToLong()} ms"
    ms < MINUTE -> "${(ms / SECOND).twoDecimals()} s"
    else -> {
        val totalSeconds = (ms / SECOND).roundToLong()
        "${totalSeconds / 60} min ${(totalSeconds % 60).toString().padStart(2, '0')} s"
    }
}

/**
 * The time between an event and the previous one, ex: "+488 ms", "+1.70 s".
 */
internal fun formatDelta(deltaMs: Long): String = "+" + formatElapsed(deltaMs.coerceAtLeast(0).toDouble())

private fun Double.twoDecimals(): String {
    val hundredths = (this * 100).roundToLong()
    return "${hundredths / 100}.${(hundredths % 100).toString().padStart(2, '0')}"
}
