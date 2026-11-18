package io.github.openflocon.domain.common.time

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.round
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.Instant

/** `212ms`, `1.2s`, `10s`, `1m 5s`: milliseconds under a second, then the biggest readable unit */
fun formatDuration(duration: Double): String {
    if (duration < 1_000) return duration.milliseconds.toString(unit = DurationUnit.MILLISECONDS)

    val seconds = duration / 1_000
    if (seconds < 60) {
        val tenths = round(seconds * 10).toLong()
        return if (tenths % 10 == 0L) "${tenths / 10}s" else "${tenths / 10}.${tenths % 10}s"
    }

    val totalSeconds = round(seconds).toLong()
    val minutes = totalSeconds / 60
    val remaining = totalSeconds % 60
    return if (remaining == 0L) "${minutes}m" else "${minutes}m ${remaining}s"
}

fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val hours = localDateTime.hour
    val minutes = localDateTime.minute
    val seconds = localDateTime.second
    val millis = localDateTime.nanosecond / 1_000_000

    return "${padZero(hours, 2)}:${padZero(minutes, 2)}:${padZero(seconds, 2)}.${
        padZero(
            millis,
            3,
        )
    }"
}

fun formatDate(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val day = localDateTime.dayOfMonth
    val month = localDateTime.monthNumber
    val year = localDateTime.year

    val hours = localDateTime.hour
    val minutes = localDateTime.minute
    val seconds = localDateTime.second

    val millis = localDateTime.nanosecond / 1_000_000

    return "${padZero(day, 2)}/${padZero(month, 2)}/$year " +
        "${padZero(hours, 2)}:${padZero(minutes, 2)}:${padZero(seconds, 2)}." +
        padZero(millis, 3)
}

fun padZero(
    number: Number,
    length: Int,
): String {
    val str = number.toString()
    return if (str.length >= length) str else "0".repeat(length - str.length) + str
}
