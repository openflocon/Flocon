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
    is FloconNetworkCallDomainModel.Request.SpecificInfos.WebSocket -> extractDomainAndPath(requestUrl)
}

private fun extractDomain(url: String): String {
    // Parse the URL into a Url object
    val parsedUrl = Url(url)

    // Use host for the domain
    val domainAndPath = parsedUrl.host

    // The code below could also work, but host is more precise for the domain
    // return parsedUrl.hostWithPort + parsedUrl.fullPath
    return domainAndPath.removePrefix("www.")
}

private fun extractDomainAndPath(url: String): String {
    // Parse the URL into a Url object
    val parsedUrl = Url(url)

    // Use host for the domain and encodedPathAndQuery for the path
    val domainAndPath = parsedUrl.host + parsedUrl.encodedPathAndQuery

    // The code below could also work, but host is more precise for the domain
    // return parsedUrl.hostWithPort + parsedUrl.fullPath
    return domainAndPath.removePrefix("www.")
}

internal fun extractPath(url: String): String {
    val parsedUrl = Url(url)
    return parsedUrl.encodedPathAndQuery
}
