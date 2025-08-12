package io.github.openflocon.data.local.network.models.graphql

data class NetworkCallGraphQlRequestEmbedded(
    val query: String,
    val operationType: String,
)
