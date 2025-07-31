package com.florent37.flocondesktop.features.network.ui.mapper

import com.florent37.flocondesktop.common.ui.ByteFormatter
import com.florent37.flocondesktop.common.ui.JsonPrettyPrinter
import com.florent37.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import com.florent37.flocondesktop.features.network.ui.model.NetworkDetailHeaderUi
import com.florent37.flocondesktop.features.network.ui.model.NetworkDetailViewState

fun toDetailUi(request: FloconHttpRequestDomainModel): NetworkDetailViewState = NetworkDetailViewState(
    fullUrl = request.url,
    method = toMethodUi(request.request.method),
    status = toNetworkStatusUi(request.response.httpCode),
    requestTimeFormatted = request.request.startTime.let { formatTimestamp(it) },
    durationFormatted = formatDuration(request.durationMs),
    // request
    requestBody = httpBodyToUi(request.request.body),
    requestHeaders = toNetworkHeadersUi(request.request.headers),
    requestSize = ByteFormatter.formatBytes(request.request.byteSize),
    // response
    responseBody = httpBodyToUi(request.response.body),
    responseHeaders = toNetworkHeadersUi(request.response.headers),
    responseSize = ByteFormatter.formatBytes(request.response.byteSize),
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
