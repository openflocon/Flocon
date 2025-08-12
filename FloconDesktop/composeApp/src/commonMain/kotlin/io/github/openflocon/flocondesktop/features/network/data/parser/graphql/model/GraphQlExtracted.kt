package io.github.openflocon.flocondesktop.features.network.data.parser.graphql.model

data class GraphQlExtracted(
    val request: Request,
) {
    data class Request(
        val requestBody: GraphQlRequestBody,
        val queryName: String?,
        val operationType: String,
    )
}
