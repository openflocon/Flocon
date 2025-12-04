package io.github.openflocon.domain.common.time

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.Instant

fun formatDuration(duration: Double): String = duration.milliseconds.toString(unit = DurationUnit.MILLISECONDS)

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

    return "${padZero(day, 2)}/${padZero(month, 2)}/${year} " +
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
