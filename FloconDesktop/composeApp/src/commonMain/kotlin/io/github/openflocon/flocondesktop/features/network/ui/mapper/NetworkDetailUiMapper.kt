package io.github.openflocon.flocondesktop.features.network.ui.mapper

import io.github.openflocon.flocondesktop.common.ui.ByteFormatter
import io.github.openflocon.flocondesktop.common.ui.JsonPrettyPrinter
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkDetailHeaderUi
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkStatusUi

fun toDetailUi(request: FloconHttpRequestDomainModel): NetworkDetailViewState = NetworkDetailViewState(
    fullUrl = request.url,
    method = toMethodUi(request.request.method),
    status = toNetworkStatusUi(request.type),
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

    graphQlSection = graphQlSection(request),
)

fun toNetworkStatusUi(type: FloconHttpRequestDomainModel.Type): NetworkStatusUi {
    return when(type) {
        is FloconHttpRequestDomainModel.Type.GraphQl -> toNetworkStatusUi(code = type.httpCode)
        is FloconHttpRequestDomainModel.Type.Grpc -> toGraphQlNetworkStatusUi(isSuccess = type.status == "OK")
        is FloconHttpRequestDomainModel.Type.Http -> toNetworkStatusUi(code = type.httpCode)
    }
}

fun graphQlSection(request: FloconHttpRequestDomainModel): NetworkDetailViewState.GraphQlSection? = (request.type as? FloconHttpRequestDomainModel.Type.GraphQl)?.let {
    NetworkDetailViewState.GraphQlSection(
        queryName = request.type.query,
        method = getMethodUi(request),
        status = toGraphQlNetworkStatusUi(isSuccess = request.type.isSuccess),
    )
}

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
