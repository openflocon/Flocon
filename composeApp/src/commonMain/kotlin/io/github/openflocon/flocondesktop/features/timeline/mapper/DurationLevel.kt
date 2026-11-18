package io.github.openflocon.flocondesktop.features.timeline.mapper

internal enum class DurationLevel { Normal, Slow, VerySlow }

private const val SLOW_MS = 1_000.0
private const val VERY_SLOW_MS = 3_000.0

/**
 * How worrying the duration of a call is: the slow ones are colored in the timeline.
 */
internal fun durationLevel(ms: Double?): DurationLevel = when {
    ms == null -> DurationLevel.Normal
    ms >= VERY_SLOW_MS -> DurationLevel.VerySlow
    ms >= SLOW_MS -> DurationLevel.Slow
    else -> DurationLevel.Normal
}
