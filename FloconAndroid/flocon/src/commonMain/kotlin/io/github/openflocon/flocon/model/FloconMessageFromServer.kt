package io.github.openflocon.flocon.model

import kotlinx.serialization.Serializable

@Serializable
internal data class FloconMessageFromServer(
    val plugin: String,
    val method: String,
    val body: String,
)