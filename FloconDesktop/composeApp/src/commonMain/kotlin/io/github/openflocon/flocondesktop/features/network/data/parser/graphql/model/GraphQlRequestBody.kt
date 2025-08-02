package io.github.openflocon.flocondesktop.features.network.data.parser.graphql.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GraphQlRequestBody(
    val query: String,
    val variables: JsonElement? = null,
)
