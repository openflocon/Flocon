package io.github.openflocon.flocon.pluginsold.network.model

data class FloconNetworkCallRequest(
    val floconCallId: String,
    val request: FloconNetworkRequest,
    val floconNetworkType: String,
    val isMocked: Boolean,
)