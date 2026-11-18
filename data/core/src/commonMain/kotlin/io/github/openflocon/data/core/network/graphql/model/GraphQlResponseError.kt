package io.github.openflocon.data.core.network.graphql.model

import kotlinx.serialization.Serializable

@Serializable
data class GraphQlResponseError(
    val message: String,
)
