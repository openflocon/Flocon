package io.github.openflocon.flocondesktop.features.graphql.ui.mapper

import com.florent37.flocondesktop.common.ui.ByteFormatter
import com.florent37.flocondesktop.features.graphql.domain.model.GraphQlRequestDomainModel
import com.florent37.flocondesktop.features.graphql.ui.model.GraphQlItemViewState
import com.florent37.flocondesktop.features.graphql.ui.model.GraphQlMethodUi
import com.florent37.flocondesktop.features.graphql.ui.model.GraphQlStatusUi
import com.florent37.flocondesktop.features.network.ui.mapper.formatDuration
import com.florent37.flocondesktop.features.network.ui.mapper.formatTimestamp

fun listToUi(httpRequests: List<GraphQlRequestDomainModel>): List<GraphQlItemViewState> = httpRequests.map { toUi(it) }

fun toUi(httpRequest: GraphQlRequestDomainModel): GraphQlItemViewState = GraphQlItemViewState(
    uuid = httpRequest.uuid,
    dateFormatted = formatTimestamp(httpRequest.infos.request.startTime),
    method = toMethodUi(httpRequest.infos.request.method),
    graphQlStatusUi = toGraphQlStatusUi(code = 200),
    route = httpRequest.infos.url,
    timeFormatted = formatDuration(httpRequest.infos.durationMs),
    requestSize = ByteFormatter.formatBytes(httpRequest.infos.request.byteSize),
    responseSize = ByteFormatter.formatBytes(httpRequest.infos.response.byteSize),
)

fun toGraphQlStatusUi(code: Int): GraphQlStatusUi = GraphQlStatusUi(
    code = code,
    isSuccess = code >= 200 && code < 300,
)

fun toMethodUi(httpMethod: String): GraphQlMethodUi = when (httpMethod.lowercase()) {
    "get" -> GraphQlMethodUi.GET
    "post" -> GraphQlMethodUi.POST
    else -> GraphQlMethodUi.Other(httpMethod)
}
