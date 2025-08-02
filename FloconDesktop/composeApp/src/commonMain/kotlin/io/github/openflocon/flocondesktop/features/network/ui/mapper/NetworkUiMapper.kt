package io.github.openflocon.flocondesktop.features.network.ui.mapper

import io.github.openflocon.flocondesktop.common.ui.ByteFormatter
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkStatusUi
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

fun toTypeUi(httpRequest: FloconHttpRequestDomainModel): NetworkItemViewState.NetworkTypeUi = when (val t = httpRequest.type) {
    is FloconHttpRequestDomainModel.Type.GraphQl -> NetworkItemViewState.NetworkTypeUi.GraphQl(
        queryName = t.query,
    )

    FloconHttpRequestDomainModel.Type.Http -> {
        val query = extractPath(httpRequest.url)
        NetworkItemViewState.NetworkTypeUi.Url(
            query = query,
        )
    }
}

fun getMethodUi(httpRequest: FloconHttpRequestDomainModel): NetworkMethodUi = when (val t = httpRequest.type) {
    is FloconHttpRequestDomainModel.Type.GraphQl -> when (t.operationType.lowercase()) {
        "query" -> NetworkMethodUi.GraphQl.QUERY
        "mutation" -> NetworkMethodUi.GraphQl.MUTATION
        else -> NetworkMethodUi.OTHER(t.operationType, icon = null)
    }
    is FloconHttpRequestDomainModel.Type.Http -> toMethodUi(httpRequest.request.method)
}

fun getStatusUi(httpRequest: FloconHttpRequestDomainModel): NetworkStatusUi = when (val t = httpRequest.type) {
    is FloconHttpRequestDomainModel.Type.GraphQl -> toGraphQlNetworkStatusUi(httpRequest.response.httpCode, isSuccess = true) // TODO
    is FloconHttpRequestDomainModel.Type.Http -> toNetworkStatusUi(httpRequest.response.httpCode)
}

fun getDomainUi(httpRequest: FloconHttpRequestDomainModel): String = when (val t = httpRequest.type) {
    is FloconHttpRequestDomainModel.Type.GraphQl -> extractDomainAndPath(httpRequest.url)
    is FloconHttpRequestDomainModel.Type.Http -> extractDomain(httpRequest.url)
}

fun toNetworkStatusUi(code: Int): NetworkStatusUi = NetworkStatusUi(
    text = code.toString(),
    isSuccess = code >= 200 && code < 300,
)

fun toGraphQlNetworkStatusUi(code: Int, isSuccess: Boolean): NetworkStatusUi {
    val isSuccess = if (code >= 200 && code < 300) {
        isSuccess
    } else false
    return NetworkStatusUi(
        text = if (isSuccess) "Success" else "Error",
        isSuccess = isSuccess,
    )
}

fun toMethodUi(httpMethod: String): NetworkMethodUi = when (httpMethod.lowercase()) {
    "get" -> NetworkMethodUi.Http.GET
    "put" -> NetworkMethodUi.Http.PUT
    "post" -> NetworkMethodUi.Http.POST
    "delete" -> NetworkMethodUi.Http.DELETE
    else -> NetworkMethodUi.OTHER(httpMethod, icon = null)
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
