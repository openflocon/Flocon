package io.github.openflocon.flocondesktop.features.network.detail.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkStatusUi
import kotlinx.serialization.Serializable

@Immutable
@Serializable
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
    // response
    val response: Response?,
) {
    @Immutable
    @Serializable
    sealed interface Response {
        @Immutable
        @Serializable
        data class Success(
            val body: String,
            val responseBodyIsNotBlank: Boolean,
            val canOpenResponseBody: Boolean,
            val size: String,
            val headers: List<NetworkDetailHeaderUi>?,
        ) : Response
        @Immutable
        @Serializable
        data class Error(
            val issue: String,
        ) : Response
    }

    @Immutable
    @Serializable
    data class GraphQlSection(
        val queryName: String,
        val method: NetworkMethodUi,
        val status: NetworkStatusUi,
    )

    @Immutable
    @Serializable
    sealed interface Method {
        @Immutable
        @Serializable
        data class Http(val method: NetworkMethodUi) : Method

        @Immutable
        @Serializable
        data class MethodName(val name: String) : Method
    }
}
