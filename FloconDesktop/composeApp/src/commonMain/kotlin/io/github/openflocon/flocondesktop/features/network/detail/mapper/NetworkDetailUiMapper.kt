package io.github.openflocon.flocondesktop.features.network.detail.mapper

import io.github.openflocon.domain.common.time.formatDuration
import io.github.openflocon.domain.common.time.formatTimestamp
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.flocondesktop.common.ui.JsonPrettyPrinter
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailHeaderUi
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState.Method.*
import io.github.openflocon.flocondesktop.features.network.list.mapper.getMethodUi
import io.github.openflocon.flocondesktop.features.network.list.mapper.loadingStatus
import io.github.openflocon.flocondesktop.features.network.list.mapper.toGraphQlNetworkStatusUi
import io.github.openflocon.flocondesktop.features.network.list.mapper.toGrpcNetworkStatusUi
import io.github.openflocon.flocondesktop.features.network.list.mapper.toHttpMethodUi
import io.github.openflocon.flocondesktop.features.network.list.mapper.toNetworkStatusUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkStatusUi

fun toDetailUi(request: FloconNetworkCallDomainModel): NetworkDetailViewState = NetworkDetailViewState(
    callId = request.callId,
    fullUrl = request.request.url,
    method = toDetailMethodUi(request),
    status = toDetailHttpStatusUi(request),
    requestTimeFormatted = request.request.startTimeFormatted,
    durationFormatted = request.response?.durationFormatted,
    // request
    requestBody = httpBodyToUi(request.request.body),
    requestHeaders = toNetworkHeadersUi(request.request.headers),
    requestSize = request.request.byteSizeFormatted,
    // response
    response = request.response?.let {
        when(it) {
            is FloconNetworkCallDomainModel.Response.Failure -> NetworkDetailViewState.Response.Error(
                issue = it.issue,
            )
            is FloconNetworkCallDomainModel.Response.Success -> NetworkDetailViewState.Response.Success(
                body = httpBodyToUi(it.body),
                size = it.byteSizeFormatted,
                headers = toNetworkHeadersUi(it.headers),
            )

        }

    },
    graphQlSection = graphQlSection(request),
)

private fun toDetailHttpStatusUi(networkCall: FloconNetworkCallDomainModel): NetworkStatusUi = networkCall.response?.let { response ->
    when (response) {
        is FloconNetworkCallDomainModel.Response.Failure -> NetworkStatusUi(
            text = response.issue,
            status = NetworkStatusUi.Status.ERROR,
        )
        is FloconNetworkCallDomainModel.Response.Success -> when(val s = response.specificInfos) {
            is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Grpc -> toGrpcNetworkStatusUi(networkCall)
            // here for grphql we want the http code, the graphql status will be displayed on the specific graphql section
            is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.GraphQl -> toNetworkStatusUi(code = s.httpCode)
            is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http -> toNetworkStatusUi(code = s.httpCode)
        }
    }
} ?: loadingStatus()

fun graphQlSection(networkCall: FloconNetworkCallDomainModel): NetworkDetailViewState.GraphQlSection? {
    return (networkCall.request.specificInfos as? FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl)?.let {
        NetworkDetailViewState.GraphQlSection(
            queryName = it.query,
            method = getMethodUi(networkCall),
            status = graphQlStatus(networkCall) ?: return null
        )
    }
}

private fun graphQlStatus(networkCall: FloconNetworkCallDomainModel) : NetworkStatusUi? {
    return when(val r = networkCall.response) {
        is FloconNetworkCallDomainModel.Response.Failure -> NetworkStatusUi(
            text = r.issue,
            status = NetworkStatusUi.Status.ERROR,
        )
        is FloconNetworkCallDomainModel.Response.Success -> when(val s = r.specificInfos) {
            is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.GraphQl -> toGraphQlNetworkStatusUi(isSuccess = s.isSuccess)
            else -> null
        }
        null -> loadingStatus()
    }
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

fun toDetailMethodUi(request: FloconNetworkCallDomainModel): NetworkDetailViewState.Method = when (request.request.specificInfos) {
    is FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc -> MethodName(
        name = request.request.method,
    )

    is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl,
    is FloconNetworkCallDomainModel.Request.SpecificInfos.Http,
    -> Http(toHttpMethodUi(request.request.method))

    FloconNetworkCallDomainModel.Request.SpecificInfos.WebSocket -> MethodName(
        name = request.request.method, // TODO
    )
}
