package io.github.openflocon.data.local.network.models.badquality

import kotlinx.serialization.Serializable

@Serializable
data class ErrorEmbedded(
    val weight: Float,
    val httpCode: Int,
    val body: String,
    val contentType: String,
)
