package io.github.openflocon.flocondesktop.features.graphql.ui.mapper

import io.github.openflocon.flocondesktop.common.ui.ByteFormatter
import io.github.openflocon.flocondesktop.common.ui.JsonPrettyPrinter
import io.github.openflocon.flocondesktop.features.graphql.domain.model.GraphQlRequestDomainModel
import io.github.openflocon.flocondesktop.features.graphql.ui.model.GraphQlDetailHeaderUi
import io.github.openflocon.flocondesktop.features.graphql.ui.model.GraphQlDetailViewState
import io.github.openflocon.flocondesktop.features.network.ui.mapper.formatDuration
import io.github.openflocon.flocondesktop.features.network.ui.mapper.formatTimestamp

fun toDetailUi(request: GraphQlRequestDomainModel): GraphQlDetailViewState = GraphQlDetailViewState(
    fullUrl = request.infos.url,
    method = toMethodUi(request.infos.request.method),
    status = toGraphQlStatusUi(request.infos.response.httpCode),
    requestTimeFormatted = request.infos.request.startTime.let { formatTimestamp(it) },
    durationFormatted = formatDuration(request.infos.durationMs),
    // request
    requestBody = httpBodyToUi(request.infos.request.body),
    requestHeaders = toGraphQlHeadersUi(request.infos.request.headers),
    requestSize = ByteFormatter.formatBytes(request.infos.request.byteSize),
    // response
    responseBody = httpBodyToUi(request.infos.response.body),
    responseHeaders = toGraphQlHeadersUi(request.infos.response.headers),
    responseSize = ByteFormatter.formatBytes(request.infos.response.byteSize),
)

fun httpBodyToUi(body: String?): String = body?.let { JsonPrettyPrinter.prettyPrint(body) } ?: ""

fun toGraphQlHeadersUi(headers: Map<String, String>?): List<GraphQlDetailHeaderUi> = headers?.let {
    it
        .map { (key, value) ->
            GraphQlDetailHeaderUi(
                name = key,
                value = value,
            )
        }.sortedBy { it.name }
} ?: emptyList()
