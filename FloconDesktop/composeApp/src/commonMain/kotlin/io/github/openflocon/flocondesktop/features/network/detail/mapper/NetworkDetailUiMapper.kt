package io.github.openflocon.flocondesktop.features.network.detail.mapper

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.isImage
import io.github.openflocon.flocondesktop.common.ui.JsonPrettyPrinter
import io.github.openflocon.flocondesktop.common.utils.OpenFile
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailHeaderUi
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState.Method.Http
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState.Method.MethodName
import io.github.openflocon.flocondesktop.features.network.list.mapper.getMethodUi
import io.github.openflocon.flocondesktop.features.network.list.mapper.loadingStatus
import io.github.openflocon.flocondesktop.features.network.list.mapper.toGraphQlNetworkStatusUi
import io.github.openflocon.flocondesktop.features.network.list.mapper.toGrpcNetworkStatusUi
import io.github.openflocon.flocondesktop.features.network.list.mapper.toHttpMethodUi
import io.github.openflocon.flocondesktop.features.network.list.mapper.toNetworkStatusUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkStatusUi
import io.github.openflocon.library.designsystem.common.isImageUrl
import kotlinx.collections.immutable.toPersistentMap

fun FloconNetworkCallDomainModel.toDetailUi(): NetworkDetailViewState {
    val imageUrl = request.url.takeIf { response?.isImage() == true || it.isImageUrl() }
    return NetworkDetailViewState(
        callId = callId,
        fullUrl = request.url,
        method = toDetailMethodUi(this),
        statusLabel = toDetailStatusLabel(this),
        status = toDetailHttpStatusUi(this),
        requestTimeFormatted = request.startTimeFormatted,
        durationFormatted = response?.durationFormatted,
        imageUrl = imageUrl,
        imageHeaders = request.headers.takeIf { imageUrl != null }?.toPersistentMap(),
        // request
        requestBodyTitle = requestBodyTitle(this),
        requestBody = httpBodyToUi(request.body),
        requestBodyIsNotBlank = request.body.isNullOrBlank().not(),
        canOpenRequestBody = canOpenExternal(request.body),
        // headers.,
        requestHeaders = toNetworkHeadersUi(request.headers),
        requestSize = request.byteSizeFormatted,
        // response
        response = response?.let {
            when (it) {
                is FloconNetworkCallDomainModel.Response.Failure -> NetworkDetailViewState.Response.Error(
                    issue = it.issue,
                )

                is FloconNetworkCallDomainModel.Response.Success -> NetworkDetailViewState.Response.Success(
                    body = httpBodyToUi(it.body),
                    size = it.byteSizeFormatted,
                    canOpenResponseBody = canOpenExternal(it.body),
                    responseBodyIsNotBlank = it.body.isNullOrBlank().not(),
                    headers = toNetworkHeadersUi(it.headers),
                )

            }

        },
        graphQlSection = graphQlSection(this),
    )
}

private fun toDetailStatusLabel(request: FloconNetworkCallDomainModel): String =
    when (request.request.specificInfos) {
        is FloconNetworkCallDomainModel.Request.SpecificInfos.WebSocket -> "Event"
        is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl,
        FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc,
        FloconNetworkCallDomainModel.Request.SpecificInfos.Http -> "Status"
    }

private fun requestBodyTitle(request: FloconNetworkCallDomainModel): String =
    when (request.request.specificInfos) {
        is FloconNetworkCallDomainModel.Request.SpecificInfos.WebSocket -> "Content"
        is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl,
        FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc,
        FloconNetworkCallDomainModel.Request.SpecificInfos.Http -> "Request - Body"
    }

private fun toDetailHttpStatusUi(networkCall: FloconNetworkCallDomainModel): NetworkStatusUi =
    networkCall.response?.let { response ->
        when (response) {
            is FloconNetworkCallDomainModel.Response.Failure -> NetworkStatusUi(
                text = response.issue,
                status = NetworkStatusUi.Status.ERROR,
            )

            is FloconNetworkCallDomainModel.Response.Success -> when (val s =
                response.specificInfos) {
                is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Grpc -> toGrpcNetworkStatusUi(
                    networkCall
                )
                // here for grphql we want the http code, the graphql status will be displayed on the specific graphql section
                is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.GraphQl -> toNetworkStatusUi(
                    code = s.httpCode
                )

                is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http -> toNetworkStatusUi(
                    code = s.httpCode
                )
            }
        }
    } ?: when (val s = networkCall.request.specificInfos) {
        is FloconNetworkCallDomainModel.Request.SpecificInfos.WebSocket -> {
            NetworkStatusUi(
                text = s.event,
                status = if (s.event == "error") NetworkStatusUi.Status.ERROR else NetworkStatusUi.Status.SUCCESS,
            )
        }

        is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl,
        is FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc,
        is FloconNetworkCallDomainModel.Request.SpecificInfos.Http -> loadingStatus()
    }

fun graphQlSection(networkCall: FloconNetworkCallDomainModel): NetworkDetailViewState.GraphQlSection? {
    return (networkCall.request.specificInfos as? FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl)?.let {
        NetworkDetailViewState.GraphQlSection(
            queryName = it.query,
            method = getMethodUi(networkCall),
            status = graphQlStatus(networkCall) ?: return null
        )
    }
}

private fun graphQlStatus(networkCall: FloconNetworkCallDomainModel): NetworkStatusUi? {
    return when (val r = networkCall.response) {
        is FloconNetworkCallDomainModel.Response.Failure -> NetworkStatusUi(
            text = r.issue,
            status = NetworkStatusUi.Status.ERROR,
        )

        is FloconNetworkCallDomainModel.Response.Success -> when (val s = r.specificInfos) {
            is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.GraphQl -> toGraphQlNetworkStatusUi(
                isSuccess = s.isSuccess
            )

            else -> null
        }

        null -> loadingStatus()
    }
}

fun httpBodyToUi(body: String?): String = body?.let { JsonPrettyPrinter.prettyPrint(body) } ?: ""

fun toNetworkHeadersUi(headers: Map<String, String>?): List<NetworkDetailHeaderUi>? = headers?.let {
    it
        .map { (key, value) ->
            NetworkDetailHeaderUi(
                name = key,
                value = value,
            )
        }.sortedBy { it.name }
}?.takeIf { it.isNotEmpty() }

fun toDetailMethodUi(request: FloconNetworkCallDomainModel): NetworkDetailViewState.Method =
    when (request.request.specificInfos) {
        is FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc -> MethodName(
            name = request.request.method,
        )

        is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl,
        is FloconNetworkCallDomainModel.Request.SpecificInfos.Http,
            -> Http(toHttpMethodUi(request.request.method))

        is FloconNetworkCallDomainModel.Request.SpecificInfos.WebSocket -> Http(
            NetworkMethodUi.WebSocket
        )
    }

private fun canOpenExternal(body: String?): Boolean {
    return body != null && body.isNotBlank() && OpenFile.isSupported()
}
