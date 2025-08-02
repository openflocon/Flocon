package io.github.openflocon.flocondesktop.features.network.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class NetworkItemViewState(
    val uuid: String,
    val dateFormatted: String,
    val requestSize: String,
    val responseSize: String,
    val timeFormatted: String,
    val type: NetworkTypeUi,
    val domain: String,
    val status: NetworkStatusUi,
    val method: NetworkMethodUi,
) {

    fun contains(text: String) : Boolean {
        return type.contains(text) ||
            domain.contains(text, ignoreCase = true) ||
            timeFormatted.contains(text, ignoreCase = true) ||
            dateFormatted.contains(text, ignoreCase = true) ||
            status.text.contains(text, ignoreCase = true) ||
            method.text.contains(text, ignoreCase = true)
    }

    sealed interface NetworkTypeUi {
        fun contains(text: String): Boolean

        @Immutable
        data class Url(
            val query: String,
        ) : NetworkTypeUi {
            override fun contains(text: String): Boolean {
                return query.contains(text, ignoreCase = true)
            }
        }
        @Immutable
        data class GraphQl(
            val queryName: String,
        ) : NetworkTypeUi {
            override fun contains(text: String): Boolean {
                return queryName.contains(text, ignoreCase = true)
            }
        }
    }
}

fun previewNetworkItemViewState(): NetworkItemViewState = NetworkItemViewState(
    uuid = "0",
    dateFormatted = "00:00:00.0000",
    requestSize = "10.kb",
    responseSize = "0.B",
    timeFormatted = "333ms",
    domain ="google.com",
    method = NetworkMethodUi.Http.GET,
    status = NetworkStatusUi("200", true),
    type = NetworkItemViewState.NetworkTypeUi.Url(
        query = "/search?q=test",
    ),
)

fun previewGraphQlItemViewState(): NetworkItemViewState = NetworkItemViewState(
    uuid = "0",
    dateFormatted = "00:00:00.0000",
    requestSize = "10.kb",
    responseSize = "0.B",
    timeFormatted = "333ms",
    domain ="google.com",
    method = NetworkMethodUi.GraphQl.QUERY,
    status = NetworkStatusUi("Success", true),
    type = NetworkItemViewState.NetworkTypeUi.GraphQl(
        queryName = "GetUserInfoQuery",
    ),
)
