package io.github.openflocon.flocondesktop.features.network.ui.mapper

import io.github.openflocon.domain.network.models.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.common.ui.ByteFormatter
import io.github.openflocon.flocondesktop.common.ui.JsonPrettyPrinter
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkDetailHeaderUi
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkStatusUi

fun toDetailUi(request: FloconHttpRequestDomainModel): NetworkDetailViewState = NetworkDetailViewState(
    uuid = request.uuid,
    fullUrl = request.url,
    method = toDetailMethodUi(request),
    status = toDetailNetworkStatusUi(request.type),
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

private fun toDetailNetworkStatusUi(type: FloconHttpRequestDomainModel.Type): NetworkStatusUi = when (type) {
    is FloconHttpRequestDomainModel.Type.Grpc -> toGrpcNetworkStatusUi(type)
    // here for grphql we want the http code, the graphql status will be displayed on the specific graphql section
    is FloconHttpRequestDomainModel.Type.GraphQl -> toNetworkStatusUi(code = type.httpCode)
    is FloconHttpRequestDomainModel.Type.Http -> toNetworkStatusUi(code = type.httpCode)
}

fun graphQlSection(request: FloconHttpRequestDomainModel): NetworkDetailViewState.GraphQlSection? =
    (request.type as? FloconHttpRequestDomainModel.Type.GraphQl)?.let {
        NetworkDetailViewState.GraphQlSection(
            queryName = it.query,
            method = getMethodUi(request),
            status = toGraphQlNetworkStatusUi(isSuccess = it.isSuccess),
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

fun toDetailMethodUi(request: FloconHttpRequestDomainModel): NetworkDetailViewState.Method = when (request.type) {
    is FloconHttpRequestDomainModel.Type.Grpc -> NetworkDetailViewState.Method.MethodName(
        name = request.request.method,
    )

    is FloconHttpRequestDomainModel.Type.GraphQl,
    is FloconHttpRequestDomainModel.Type.Http,
        -> NetworkDetailViewState.Method.Http(toHttpMethodUi(request.request.method))
}
