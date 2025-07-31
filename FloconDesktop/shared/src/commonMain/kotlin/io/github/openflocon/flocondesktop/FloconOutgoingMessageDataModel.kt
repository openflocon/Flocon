package io.github.openflocon.flocondesktop

import kotlinx.serialization.Serializable

@Serializable
data class FloconOutgoingMessageDataModel(
    val plugin: String,
    val method: String,
    val body: String,
)
