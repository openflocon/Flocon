package com.florent37.flocondesktop.features.grpc.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class GrpcItemViewState(
    val callId: String,

    val requestTimeFormatted: String,
    val url: String, // authority
    val method: String,
    val status: StatusViewState,
    val durationFormatted: String?,
) {
    @Immutable
    sealed interface StatusViewState {
        val text: String

        @Immutable
        data class Success(override val text: String) : StatusViewState

        @Immutable
        data class Waiting(override val text: String) : StatusViewState

        @Immutable
        data class Failure(override val text: String) : StatusViewState
    }

    fun contains(text: String): Boolean = listOf(callId, requestTimeFormatted, url, method, status.text, durationFormatted).any {
        it?.contains(text, ignoreCase = true) == true
    }
}

fun previewGrpcItemViewState(): GrpcItemViewState = GrpcItemViewState(
    callId = "0",
    requestTimeFormatted = "00:00:00.0000",
    url = "google.com.test",
    method = "public.get.methodName",
    status = GrpcItemViewState.StatusViewState.Success("OK"),
    durationFormatted = "333ms",
)
