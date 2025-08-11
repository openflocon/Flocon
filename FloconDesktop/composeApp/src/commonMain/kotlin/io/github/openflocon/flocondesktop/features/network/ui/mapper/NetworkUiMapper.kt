package io.github.openflocon.flocondesktop.features.network.ui.mapper

import io.github.openflocon.domain.common.ui.ByteFormatter
import io.github.openflocon.domain.network.models.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.ktor.http.Url
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.Instant

fun listToUi(httpRequests: List<FloconHttpRequestDomainModel>): List<NetworkItemViewState> = httpRequests.map { toUi(it) }

fun extractDomain(url: String): String {
    // Parse l'URL en un objet Url
    val parsedUrl = Url(url)

    // Utilise host pour le domaine et encodedPathAndQuery pour le chemin
    val domainAndPath = parsedUrl.host

    // Le code ci-dessous pourrait aussi fonctionner, mais host est plus précis pour le domaine
    // return parsedUrl.hostWithPort + parsedUrl.fullPath
    return domainAndPath.removePrefix("www.")
}

fun extractDomainAndPath(url: String): String {
    // Parse l'URL en un objet Url
    val parsedUrl = Url(url)

    // Utilise host pour le domaine et encodedPathAndQuery pour le chemin
    val domainAndPath = parsedUrl.host + parsedUrl.encodedPathAndQuery

    // Le code ci-dessous pourrait aussi fonctionner, mais host est plus précis pour le domaine
    // return parsedUrl.hostWithPort + parsedUrl.fullPath
    return domainAndPath.removePrefix("www.")
}

fun extractPath(url: String): String {
    val parsedUrl = Url(url)
    return parsedUrl.encodedPathAndQuery
}

fun toUi(httpRequest: FloconHttpRequestDomainModel): NetworkItemViewState = NetworkItemViewState(
    uuid = httpRequest.uuid,
    dateFormatted = formatTimestamp(httpRequest.request.startTime),
    timeFormatted = formatDuration(httpRequest.durationMs),
    requestSize = ByteFormatter.formatBytes(httpRequest.request.byteSize),
    responseSize = ByteFormatter.formatBytes(httpRequest.response.byteSize),
    domain = getDomainUi(httpRequest),
    type = toTypeUi(httpRequest),
    method = getMethodUi(httpRequest),
    status = getStatusUi(httpRequest),
)

fun getDomainUi(httpRequest: FloconHttpRequestDomainModel): String = when (val t = httpRequest.type) {
    is FloconHttpRequestDomainModel.Type.GraphQl -> extractDomainAndPath(httpRequest.url)
    is FloconHttpRequestDomainModel.Type.Http -> extractDomain(httpRequest.url)
    is FloconHttpRequestDomainModel.Type.Grpc -> httpRequest.url
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
