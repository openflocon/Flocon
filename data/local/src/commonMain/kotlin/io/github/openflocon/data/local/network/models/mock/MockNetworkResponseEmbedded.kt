package io.github.openflocon.data.local.network.models.mock

import kotlinx.serialization.Serializable

@Serializable
data class MockNetworkResponseEmbedded(
    val delay: Long,
    val type: Type,
) {
    @Serializable
    sealed interface Type {
        @Serializable
        data class Body(
            val httpCode: Int,
            val body: String,
            val mediaType: String,
            val headers: Map<String, String>,
        ) : Type

        @Serializable
        data class Exception(
            val classPath: String,
        ) : Type
    }
}
