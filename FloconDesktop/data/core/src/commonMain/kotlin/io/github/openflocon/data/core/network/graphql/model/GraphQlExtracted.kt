package io.github.openflocon.data.core.network.graphql.model

data class GraphQlExtracted(
    val request: Request,
) {
    data class Request(
        val requestBody: GraphQlRequestBody,
        val queryName: String?,
        val operationType: String,
    )
}
