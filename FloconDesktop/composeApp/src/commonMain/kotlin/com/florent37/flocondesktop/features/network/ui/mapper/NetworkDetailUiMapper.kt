package com.florent37.flocondesktop.features.network.ui.mapper

import com.florent37.flocondesktop.common.ui.ByteFormatter
import com.florent37.flocondesktop.common.ui.JsonPrettyPrinter
import com.florent37.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import com.florent37.flocondesktop.features.network.ui.model.NetworkDetailHeaderUi
import com.florent37.flocondesktop.features.network.ui.model.NetworkDetailViewState

fun toDetailUi(request: FloconHttpRequestDomainModel): NetworkDetailViewState = NetworkDetailViewState(
    fullUrl = request.infos.url ?: "",
    method = toMethodUi(request.infos.method),
    status = toNetworkStatusUi(request.infos.response.httpCode ?: 0),
    requestTimeFormatted = request.infos.startTime?.let { formatTimestamp(it) } ?: "",
    durationFormatted = formatDuration(request.infos.durationMs),
    // request
    requestBody = httpBodyToUi(request.infos.request.body),
    requestHeaders = toNetworkHeadersUi(request.infos.request.headers),
    requestSize = ByteFormatter.formatBytes(request.infos.request.byteSize), // TODO
    // response
    responseBody = httpBodyToUi(request.infos.response.body),
    responseHeaders = toNetworkHeadersUi(request.infos.response.headers),
    responseSize = ByteFormatter.formatBytes(request.infos.response.byteSize), // TODO
)

fun httpBodyToUi(body: String?): String = body?.let { JsonPrettyPrinter.prettyPrint(body) } ?: ""

fun toNetworkHeadersUi(headers: Map<String, String>?): List<NetworkDetailHeaderUi> = headers?.let {
    it
        .map { (key, value) ->
            NetworkDetailHeaderUi(
                name = key,
                value = value,
            )
        }.sortedBy { it.name }
} ?: emptyList()
