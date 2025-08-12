package io.github.openflocon.flocon.plugins.network.model

data class FloconNetworkCallRequest(
    val floconCallId: String,
    val request: FloconNetworkRequest,
    val floconNetworkType: String,
    val isMocked: Boolean,
)