package io.github.openflocon.flocondesktop.features.network.data.parser.graphql.model

import kotlinx.serialization.Serializable

@Serializable
data class GraphQlResponseError(
    val message: String,
)
