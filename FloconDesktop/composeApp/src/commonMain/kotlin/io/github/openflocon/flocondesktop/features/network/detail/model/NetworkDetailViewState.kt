package io.github.openflocon.flocondesktop.features.network.detail.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkStatusUi
import kotlinx.collections.immutable.PersistentMap

@Immutable
data class NetworkDetailViewState(
    val callId: String,
    val fullUrl: String,
    val requestTimeFormatted: String,
    val durationFormatted: String?,

    val method: Method,
    val statusLabel: String,
    val status: NetworkStatusUi,

    val graphQlSection: GraphQlSection?,

    // request
    val requestBodyTitle: String,
    val requestBody: String,
    val requestBodyIsNotBlank: Boolean,
    val canOpenRequestBody: Boolean,

    val requestSize: String,
    val requestHeaders: List<NetworkDetailHeaderUi>?,
    val imageUrl: String?, // filled only if it's an image url
    val imageHeaders: PersistentMap<String, String>?,
    // response
    val response: Response?,
) {
    @Immutable
    sealed interface Response {
        @Immutable
        data class Success(
            val body: String,
            val responseBodyIsNotBlank: Boolean,
            val canOpenResponseBody: Boolean,
            val size: String,
            val headers: List<NetworkDetailHeaderUi>?,
        ) : Response

        @Immutable
        data class Error(
            val issue: String,
        ) : Response
    }

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
