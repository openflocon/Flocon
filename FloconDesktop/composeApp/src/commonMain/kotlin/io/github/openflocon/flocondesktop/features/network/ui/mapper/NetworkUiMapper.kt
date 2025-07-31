package io.github.openflocon.flocondesktop.features.network.ui.mapper

import io.github.openflocon.flocondesktop.common.ui.ByteFormatter
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkStatusUi
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.Instant

fun listToUi(httpRequests: List<FloconHttpRequestDomainModel>): List<NetworkItemViewState> = httpRequests.map { toUi(it) }

fun toUi(httpRequest: FloconHttpRequestDomainModel): NetworkItemViewState = NetworkItemViewState(
    uuid = httpRequest.uuid,
    dateFormatted = formatTimestamp(httpRequest.request.startTime),
    method = toMethodUi(httpRequest.request.method),
    networkStatusUi = toNetworkStatusUi(code = httpRequest.response.httpCode),
    route = httpRequest.url,
    timeFormatted = formatDuration(httpRequest.durationMs),
    requestSize = ByteFormatter.formatBytes(httpRequest.request.byteSize),
    responseSize = ByteFormatter.formatBytes(httpRequest.response.byteSize),
)

fun toNetworkStatusUi(code: Int): NetworkStatusUi = NetworkStatusUi(
    code = code,
    isSuccess = code >= 200 && code < 300,
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
