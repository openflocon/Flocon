package io.github.openflocon.flocondesktop.features.network.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class NetworkItemViewState(
    val uuid: String,
    val dateFormatted: String,
    val route: Route,
    val method: NetworkMethodUi,
    val networkStatusUi: NetworkStatusUi,
    val requestSize: String,
    val responseSize: String,
    val timeFormatted: String,
) {
    sealed interface Route {
        fun contains(text: String): Boolean

        @Immutable
        data class Url(val url: String) : Route {
            override fun contains(text: String): Boolean {
                return url.contains(text, ignoreCase = true)
            }
        }
        @Immutable
        data class GraphQl(
            val queryName: String,
            val url: String,
        ) : Route {
            override fun contains(text: String): Boolean {
                return url.contains(text, ignoreCase = true) || queryName.contains(text, ignoreCase = true)
            }
        }
    }
}

fun previewNetworkItemViewState(): NetworkItemViewState = NetworkItemViewState(
    uuid = "0",
    dateFormatted = "00:00:00.0000",
    route = NetworkItemViewState.Route.Url("www.google.com.test"),
    method = NetworkMethodUi.GET,
    networkStatusUi = NetworkStatusUi(200, true),
    requestSize = "10.kb",
    responseSize = "0.B",
    timeFormatted = "333ms",
)

fun previewGraphQlItemViewState(): NetworkItemViewState = NetworkItemViewState(
    uuid = "0",
    dateFormatted = "00:00:00.0000",
    route = NetworkItemViewState.Route.GraphQl("GetUserInfoQuery", "api.github.com/graphql"),
    method = NetworkMethodUi.GET,
    networkStatusUi = NetworkStatusUi(200, true),
    requestSize = "10.kb",
    responseSize = "0.B",
    timeFormatted = "333ms",
)
