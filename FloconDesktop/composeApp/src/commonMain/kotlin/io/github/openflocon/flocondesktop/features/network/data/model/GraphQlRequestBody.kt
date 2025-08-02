package io.github.openflocon.flocondesktop.features.network.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GraphQlRequestBody(
    val query: String,
    val variables: JsonElement? = null,
)


@Serializable
data class GraphQlResponseBody(
    val errors: List<GraphQlResponseError>? = null,
    val data: JsonElement? = null,
)

@Serializable
data class GraphQlResponseError(
    val message: String,
)
