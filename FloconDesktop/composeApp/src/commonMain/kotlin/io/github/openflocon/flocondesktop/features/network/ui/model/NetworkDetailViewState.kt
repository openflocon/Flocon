package io.github.openflocon.flocondesktop.features.network.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class NetworkDetailViewState(
    val fullUrl: String,
    val requestTimeFormatted: String,
    val durationFormatted: String,

    val method: Method,
    val status: NetworkStatusUi,

    val graphQlSection: GraphQlSection?,

    // request
    val requestBody: String,
    val requestSize: String,
    val requestHeaders: List<NetworkDetailHeaderUi>,
    // response
    val responseBody: String,
    val responseSize: String,
    val responseHeaders: List<NetworkDetailHeaderUi>,
) {
    @Immutable
    data class GraphQlSection(
        val queryName: String,
        val method: NetworkMethodUi,
        val status: NetworkStatusUi,
    )

    @Immutable
    sealed interface Method {
        @Immutable
        data class Http(val method: NetworkMethodUi) : Method

        @Immutable
        data class MethodName(val name: String) : Method
    }
}
