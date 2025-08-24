package io.github.openflocon.flocondesktop.features.network.list.mapper

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.byteSize
import io.github.openflocon.flocondesktop.common.ui.ByteFormatter
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.ktor.http.Url
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.Instant

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

fun toUi(
    networkCall: FloconNetworkCallDomainModel,
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel?
): NetworkItemViewState {
    return NetworkItemViewState(
        uuid = networkCall.callId,
        dateFormatted = formatTimestamp(networkCall.request.startTime),
        timeFormatted = networkCall.response?.durationMs?.let { formatDuration(it) },
        requestSize = ByteFormatter.formatBytes(networkCall.request.byteSize),
        responseSize = networkCall.byteSize()?.let { ByteFormatter.formatBytes(it) },
        domain = getDomainUi(networkCall),
        type = toTypeUi(networkCall),
        method = getMethodUi(networkCall),
        status = getStatusUi(networkCall),
        isMocked = networkCall.request.isMocked,
        isFromOldAppInstance = deviceIdAndPackageName?.appInstance?.let { it != networkCall.appInstance } ?: false
    )
}

fun getDomainUi(networkRequest: FloconNetworkCallDomainModel): String = when (networkRequest.request.specificInfos) {
    is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl -> extractDomainAndPath(networkRequest.request.url)
    is FloconNetworkCallDomainModel.Request.SpecificInfos.Http -> extractDomain(networkRequest.request.url)
    is FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc -> networkRequest.request.url
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
