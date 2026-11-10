package io.github.openflocon.flocon.network.core.model

data class FloconNetworkCallResponse(
    val floconCallId: String,
    val response: FloconNetworkResponse,
    val durationMs: Double,
    val floconNetworkType: String,
    val isMocked: Boolean,
)
