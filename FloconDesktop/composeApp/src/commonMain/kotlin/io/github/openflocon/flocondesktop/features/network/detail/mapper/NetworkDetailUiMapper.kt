package io.github.openflocon.flocondesktop.features.network.detail.mapper

import io.github.openflocon.domain.models.settings.NetworkDetailTab
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
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentMap

fun FloconNetworkCallDomainModel.toDetailUi(
    defaultSelectedTab: NetworkDetailTab = NetworkDetailTab.Request,
): NetworkDetailViewState {
    val imageUrl = request.url.takeIf { response?.isImage() == true || it.isImageUrl() }
    val urlParts = splitUrl(request.url)
    return NetworkDetailViewState(
        callId = callId,
        fullUrl = request.url,
        host = urlParts.host,
        path = urlParts.path,
        queryParams = urlParts.queryParams.takeIf { it.isNotEmpty() }?.toImmutableList(),
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
        defaultSelectedTab = defaultSelectedTab,
    )
}

private fun toDetailStatusLabel(request: FloconNetworkCallDomainModel): String = when (request.request.specificInfos) {
    is FloconNetworkCallDomainModel.Request.SpecificInfos.WebSocket -> "Event"

    is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl,
    FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc,
    FloconNetworkCallDomainModel.Request.SpecificInfos.Http -> "Status"
}

private fun requestBodyTitle(request: FloconNetworkCallDomainModel): String = when (request.request.specificInfos) {
    is FloconNetworkCallDomainModel.Request.SpecificInfos.WebSocket -> "Content"

    is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl,
    FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc,
    FloconNetworkCallDomainModel.Request.SpecificInfos.Http -> "Body"
}

private fun toDetailHttpStatusUi(networkCall: FloconNetworkCallDomainModel): NetworkStatusUi = networkCall.response?.let { response ->
    when (response) {
        is FloconNetworkCallDomainModel.Response.Failure -> NetworkStatusUi(
            text = response.issue,
            status = NetworkStatusUi.Status.ERROR,
        )

        is FloconNetworkCallDomainModel.Response.Success -> when (
            val s =
                response.specificInfos
        ) {
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

private fun graphQlStatus(networkCall: FloconNetworkCallDomainModel): NetworkStatusUi? = when (val r = networkCall.response) {
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

fun httpBodyToUi(body: String?): String = body?.let { JsonPrettyPrinter.prettyPrint(body) } ?: ""

fun toNetworkHeadersUi(headers: Map<String, String>?): ImmutableList<NetworkDetailHeaderUi>? = headers?.let {
    it
        .map { (key, value) ->
            NetworkDetailHeaderUi(
                name = key,
                value = value,
            )
        }.sortedBy { it.name }
        .toImmutableList()
}?.takeIf { it.isNotEmpty() }

fun toDetailMethodUi(request: FloconNetworkCallDomainModel): NetworkDetailViewState.Method = when (request.request.specificInfos) {
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

private fun canOpenExternal(body: String?): Boolean = body != null && body.isNotBlank() && OpenFile.isSupported()

internal data class UrlParts(
    val host: String,
    val path: String,
    val queryParams: List<NetworkDetailHeaderUi>,
)

/**
 * Splits `scheme://host/path?a=1&b=2#fragment` for the detail header.
 * Without a scheme (grpc method, websocket event...) the whole input is the path.
 */
internal fun splitUrl(url: String): UrlParts {
    val withoutFragment = url.substringBefore('#')
    val schemeEnd = withoutFragment.indexOf("://")
    val firstDelimiter = withoutFragment.indexOfFirst { it == '/' || it == '?' }
    val hasScheme = schemeEnd > 0 && (firstDelimiter == -1 || schemeEnd < firstDelimiter)
    val withoutScheme = if (hasScheme) withoutFragment.substring(schemeEnd + 3) else withoutFragment
    val beforeQuery = withoutScheme.substringBefore('?')
    val query = withoutScheme.substringAfter('?', missingDelimiterValue = "")

    val host = if (hasScheme) beforeQuery.substringBefore('/') else ""
    val path = if (hasScheme) beforeQuery.removePrefix(host).ifEmpty { "/" } else beforeQuery

    val queryParams = query
        .split('&')
        .filter { it.isNotEmpty() && !it.startsWith('=') }
        .map { param ->
            NetworkDetailHeaderUi(
                name = param.substringBefore('='),
                value = param.substringAfter('=', missingDelimiterValue = ""),
            )
        }

    return UrlParts(host = host, path = path, queryParams = queryParams)
}
