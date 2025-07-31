package io.github.openflocon.flocondesktop.features.grpc.ui.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkDetailHeaderUi
import io.github.openflocon.flocondesktop.features.network.ui.model.previewNetworkDetailHeaderUi

@Immutable
data class GrpcDetailViewState(
    val url: String, // authority

    val requestTimeFormatted: String,
    val durationFormatted: String?,

    val method: String,
    val status: GrpcItemViewState.StatusViewState,

    // request
    val requestBody: String?,
    val requestHeaders: List<NetworkDetailHeaderUi>,

    val response: ResponseViewState?,
) {
    data class ResponseViewState(
        val headers: List<NetworkDetailHeaderUi>,
        val result: DetailPayload,
    )

    sealed interface DetailPayload {
        data class Success(val body: String) : DetailPayload
        data class Failure(val cause: String) : DetailPayload
    }
}

fun previewGrpcDetailViewState() = GrpcDetailViewState(
    url = "google.com.test",
    requestTimeFormatted = "00:00:00.0000",
    durationFormatted = "333ms",
    method = "public.get.methodName",
    status = GrpcItemViewState.StatusViewState.Success("OK"),
    requestBody = "request body",
    requestHeaders = listOf(
        previewNetworkDetailHeaderUi(),
        previewNetworkDetailHeaderUi(),
        previewNetworkDetailHeaderUi(),
        previewNetworkDetailHeaderUi(),
        previewNetworkDetailHeaderUi(),
    ),
    response = GrpcDetailViewState.ResponseViewState(
        headers = listOf(
            previewNetworkDetailHeaderUi(),
            previewNetworkDetailHeaderUi(),
            previewNetworkDetailHeaderUi(),
            previewNetworkDetailHeaderUi(),
            previewNetworkDetailHeaderUi(),
        ),
        result = GrpcDetailViewState.DetailPayload.Success("response body"),
    ),
)
