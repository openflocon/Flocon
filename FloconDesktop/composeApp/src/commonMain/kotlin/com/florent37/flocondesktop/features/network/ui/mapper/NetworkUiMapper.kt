package com.florent37.flocondesktop.features.network.ui.mapper

import com.florent37.flocondesktop.common.ui.ByteFormatter
import com.florent37.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import com.florent37.flocondesktop.features.network.ui.model.NetworkItemViewState
import com.florent37.flocondesktop.features.network.ui.model.NetworkMethodUi
import com.florent37.flocondesktop.features.network.ui.model.NetworkStatusUi
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.Instant

fun listToUi(httpRequests: List<FloconHttpRequestDomainModel>): List<NetworkItemViewState> = httpRequests.map { toUi(it) }

fun toUi(httpRequest: FloconHttpRequestDomainModel): NetworkItemViewState = NetworkItemViewState(
    uuid = httpRequest.uuid,
    dateFormatted = formatTimestamp(httpRequest.infos.startTime),
    method = toMethodUi(httpRequest.infos.method),
    networkStatusUi = toNetworkStatusUi(code = 200),
    route = httpRequest.infos.url,
    timeFormatted = formatDuration(httpRequest.infos.durationMs),
    requestSize = ByteFormatter.formatBytes(httpRequest.infos.request.byteSize),
    responseSize = ByteFormatter.formatBytes(httpRequest.infos.response.byteSize),
)

// TODO
fun toNetworkStatusUi(code: Int): NetworkStatusUi = NetworkStatusUi(
    code = code,
    isSuccess = true, // TODO
)

fun toMethodUi(httpMethod: String): NetworkMethodUi = when (httpMethod.lowercase()) {
    "get" -> NetworkMethodUi.GET
    "put" -> NetworkMethodUi.PUT
    "post" -> NetworkMethodUi.POST
    "delete" -> NetworkMethodUi.DELETE
    else -> NetworkMethodUi.OTHER(httpMethod)
}

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

fun padZero(
    number: Number,
    length: Int,
): String {
    val str = number.toString()
    return if (str.length >= length) str else "0".repeat(length - str.length) + str
}
