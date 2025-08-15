package io.github.openflocon.flocondesktop.features.network.mapper

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.flocondesktop.common.ui.ByteFormatter
import io.github.openflocon.flocondesktop.common.ui.JsonPrettyPrinter
import io.github.openflocon.flocondesktop.features.network.model.NetworkDetailHeaderUi
import io.github.openflocon.flocondesktop.features.network.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.model.NetworkStatusUi

fun toDetailUi(request: FloconNetworkCallDomainModel): NetworkDetailViewState = NetworkDetailViewState(
    callId = request.callId,
    fullUrl = request.networkRequest.url,
    method = toDetailMethodUi(request),
    status = toDetailHttpStatusUi(request),
    requestTimeFormatted = request.networkRequest.startTime.let { formatTimestamp(it) },
    durationFormatted = request.networkResponse?.durationMs?.let { formatDuration(it) },
    // request
    requestBody = httpBodyToUi(request.networkRequest.body),
    requestHeaders = toNetworkHeadersUi(request.networkRequest.headers),
    requestSize = ByteFormatter.formatBytes(request.networkRequest.byteSize),
    // response
    response = request.networkResponse?.let {
        NetworkDetailViewState.Response(
            body = httpBodyToUi(it.body),
            size = ByteFormatter.formatBytes(it.byteSize),
            headers = toNetworkHeadersUi(it.headers),
        )
    },
    graphQlSection = graphQlSection(request),
)

private fun toDetailHttpStatusUi(networkCall: FloconNetworkCallDomainModel): NetworkStatusUi = networkCall.networkResponse?.let { response ->
    when (networkCall) {
        is FloconNetworkCallDomainModel.Grpc -> toGrpcNetworkStatusUi(networkCall)
        // here for grphql we want the http code, the graphql status will be displayed on the specific graphql section
        is FloconNetworkCallDomainModel.GraphQl -> toNetworkStatusUi(code = networkCall.response!!.httpCode)
        is FloconNetworkCallDomainModel.Http -> toNetworkStatusUi(code = networkCall.response!!.httpCode)
    }
} ?: loadingStatus()

fun graphQlSection(networkCall: FloconNetworkCallDomainModel): NetworkDetailViewState.GraphQlSection? = (networkCall as? FloconNetworkCallDomainModel.GraphQl)?.let {
    NetworkDetailViewState.GraphQlSection(
        queryName = it.request.query,
        method = getMethodUi(networkCall),
        status = it.response?.isSuccess?.let {
            toGraphQlNetworkStatusUi(isSuccess = it)
        } ?: loadingStatus(),
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

fun toDetailMethodUi(request: FloconNetworkCallDomainModel): NetworkDetailViewState.Method = when (request) {
    is FloconNetworkCallDomainModel.Grpc -> NetworkDetailViewState.Method.MethodName(
        name = request.networkRequest.method,
    )

    is FloconNetworkCallDomainModel.GraphQl,
    is FloconNetworkCallDomainModel.Http,
    -> NetworkDetailViewState.Method.Http(toHttpMethodUi(request.networkRequest.method))
}
