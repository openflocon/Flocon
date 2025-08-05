package io.github.openflocon.flocondesktop.features.network.data.parser.graphql.model

data class GraphQlExtracted(
    val request: Request,
    val response: GraphQlResponseBody?,
) {
    data class Request(
        val requestBody: GraphQlRequestBody,
        val queryName: String?,
        val operationType: String,
    )
}
