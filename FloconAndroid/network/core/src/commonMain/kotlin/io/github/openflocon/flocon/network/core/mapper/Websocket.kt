package io.github.openflocon.flocon.network.core.mapper

import kotlinx.serialization.Serializable

@Serializable
internal class WebSocketMockMessage(
    val id: String,
    val message: String,
)