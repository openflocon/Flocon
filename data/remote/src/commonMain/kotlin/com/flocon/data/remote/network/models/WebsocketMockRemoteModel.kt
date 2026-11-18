package com.flocon.data.remote.network.models

import kotlinx.serialization.Serializable

@Serializable
data class WebsocketMockRemoteModel(
    val id: String,
    val message: String,
)
