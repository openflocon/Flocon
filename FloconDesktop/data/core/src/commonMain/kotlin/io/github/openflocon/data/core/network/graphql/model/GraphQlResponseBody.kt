package io.github.openflocon.data.core.network.graphql.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GraphQlResponseBody(
    val errors: List<GraphQlResponseError>? = null,
    val data: JsonElement? = null,
)
