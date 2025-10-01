package io.github.openflocon.flocondesktop.features.network.list.mapper

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.responseByteSizeFormatted
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.ktor.http.Url

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
        dateFormatted = networkCall.request.startTimeFormatted,
        timeFormatted = networkCall.response?.durationFormatted,
        requestSize = networkCall.request.byteSizeFormatted,
        responseSize = networkCall.responseByteSizeFormatted(),
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
