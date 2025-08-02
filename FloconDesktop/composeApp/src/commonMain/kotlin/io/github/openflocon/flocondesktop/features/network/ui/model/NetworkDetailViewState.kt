package io.github.openflocon.flocondesktop.features.network.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class NetworkDetailViewState(
    val fullUrl: String,
    val requestTimeFormatted: String,
    val durationFormatted: String,
    val method: NetworkMethodUi,
    val status: NetworkStatusUi,

    val graphQlSection : GraphQlSection?,

    // request
    val requestBody: String,
    val requestSize: String,
    val requestHeaders: List<NetworkDetailHeaderUi>,
    // response
    val responseBody: String,
    val responseSize: String,
    val responseHeaders: List<NetworkDetailHeaderUi>,
) {
    data class GraphQlSection(
        val queryName: String,
        val method: NetworkMethodUi,
        val status: NetworkStatusUi,
    )
}
