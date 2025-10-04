package com.flocon.data.remote.network.models

import kotlinx.serialization.Serializable

@Serializable
data class FloconNetworkWebSocketEvent(
    val id: String? = null,
    val event: String? = null,
    val url: String? = null,
    val timestamp: Long? = null,
    val message: String? = null,
    val error: String? = null
)
