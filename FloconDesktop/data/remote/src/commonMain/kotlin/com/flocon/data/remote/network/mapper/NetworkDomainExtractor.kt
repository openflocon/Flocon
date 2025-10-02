package com.flocon.data.remote.network.mapper

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.ktor.http.Url

internal fun extractDomain(
    requestUrl: String,
    specificInfos: FloconNetworkCallDomainModel.Request.SpecificInfos,
): String = when (specificInfos) {
    is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl -> extractDomainAndPath(requestUrl)
    is FloconNetworkCallDomainModel.Request.SpecificInfos.Http -> extractDomain(requestUrl)
    is FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc -> requestUrl
}

private fun extractDomain(url: String): String {
    // Parse l'URL en un objet Url
    val parsedUrl = Url(url)

    // Utilise host pour le domaine et encodedPathAndQuery pour le chemin
    val domainAndPath = parsedUrl.host

    // Le code ci-dessous pourrait aussi fonctionner, mais host est plus précis pour le domaine
    // return parsedUrl.hostWithPort + parsedUrl.fullPath
    return domainAndPath.removePrefix("www.")
}

private fun extractDomainAndPath(url: String): String {
    // Parse l'URL en un objet Url
    val parsedUrl = Url(url)

    // Utilise host pour le domaine et encodedPathAndQuery pour le chemin
    val domainAndPath = parsedUrl.host + parsedUrl.encodedPathAndQuery

    // Le code ci-dessous pourrait aussi fonctionner, mais host est plus précis pour le domaine
    // return parsedUrl.hostWithPort + parsedUrl.fullPath
    return domainAndPath.removePrefix("www.")
}

internal fun extractPath(url: String): String {
    val parsedUrl = Url(url)
    return parsedUrl.encodedPathAndQuery
}
