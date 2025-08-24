package io.github.openflocon.flocondesktop.features.network.list.model

import androidx.compose.runtime.Immutable

@Immutable
data class NetworkItemViewState(
    val uuid: String,
    val dateFormatted: String,
    val requestSize: String,
    val responseSize: String?,
    val timeFormatted: String?,
    val type: NetworkTypeUi,
    val domain: String,
    val status: NetworkStatusUi,
    val method: NetworkMethodUi,
    val isMocked: Boolean,
    val isFromOldAppInstance: Boolean,
) {

    fun contains(text: String): Boolean = type.contains(text) ||
        domain.contains(text, ignoreCase = true) ||
        timeFormatted?.contains(text, ignoreCase = true) == true ||
        dateFormatted.contains(text, ignoreCase = true) ||
        status.text.contains(text, ignoreCase = true) ||
        method.text.contains(text, ignoreCase = true)

    sealed interface NetworkTypeUi {

        val text: String

        fun contains(text: String): Boolean

        @Immutable
        data class Url(
            val method: String,
            val query: String,
        ) : NetworkTypeUi {
            override fun contains(text: String): Boolean = query.contains(text, ignoreCase = true)
            override val text = query
        }

        @Immutable
        data class GraphQl(
            val queryName: String,
        ) : NetworkTypeUi {
            override fun contains(text: String): Boolean = queryName.contains(text, ignoreCase = true)
            override val text = queryName
        }

        @Immutable
        data class Grpc(
            val method: String,
        ) : NetworkTypeUi {
            override fun contains(text: String): Boolean = method.contains(text, ignoreCase = true)
            override val text = method
        }
    }
}

fun previewNetworkItemViewState(): NetworkItemViewState = NetworkItemViewState(
    uuid = "0",
    dateFormatted = "00:00:00.0000",
    requestSize = "10.kb",
    responseSize = "0.B",
    timeFormatted = "333ms",
    domain = "google.com",
    method = NetworkMethodUi.Http.GET,
    status = NetworkStatusUi("200", NetworkStatusUi.Status.SUCCESS),
    type = NetworkItemViewState.NetworkTypeUi.Url(
        query = "/search?q=test",
        method = "get",
    ),
    isMocked = false,
    isFromOldAppInstance = false,
)

fun previewGraphQlItemViewState(): NetworkItemViewState = NetworkItemViewState(
    uuid = "0",
    dateFormatted = "00:00:00.0000",
    requestSize = "10.kb",
    responseSize = "0.B",
    timeFormatted = "333ms",
    domain = "google.com",
    method = NetworkMethodUi.GraphQl.QUERY,
    status = NetworkStatusUi("Success", status = NetworkStatusUi.Status.SUCCESS),
    type = NetworkItemViewState.NetworkTypeUi.GraphQl(
        queryName = "GetUserInfoQuery",
    ),
    isMocked = false,
    isFromOldAppInstance = false,
)
