package io.github.openflocon.data.core.network.graphql.model

sealed interface GraphQlExtracted {
    data class WithBody(
        val requestBody: GraphQlRequestBody,
        val queryName: String?,
        val operationType: String,
    ) : GraphQlExtracted

    data class PersistedQuery(
        val queryName: String,
        val operationType: String,
    ) : GraphQlExtracted
}
