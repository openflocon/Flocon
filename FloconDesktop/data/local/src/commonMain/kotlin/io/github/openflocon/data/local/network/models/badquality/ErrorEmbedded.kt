package io.github.openflocon.data.local.network.models.badquality

import kotlinx.serialization.Serializable

@Serializable
data class ErrorEmbedded(
    val weight: Float,
    val type: Type,
) {
    @Serializable
    sealed interface Type {
        @Serializable
        data class Body(
            val httpCode: Int,
            val body: String,
            val contentType: String,
        ) : Type
        @Serializable
        data class Exception(
            val classPath: String,
        ) : Type
    }
}
