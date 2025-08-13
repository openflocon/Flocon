package io.github.openflocon.data.core.network.graphql

import io.github.openflocon.data.core.network.graphql.model.GraphQlExtracted
import io.github.openflocon.data.core.network.graphql.model.GraphQlRequestBody
import io.github.openflocon.data.core.network.graphql.model.GraphQlResponseBody
import io.github.openflocon.domain.network.models.FloconNetworkRequestDomainModel
import kotlinx.serialization.json.Json

// TODO Move

private val graphQlParser =
    Json {
        ignoreUnknownKeys = true
    }

// maybe use graphql-java
fun extractGraphQl(decoded: FloconNetworkRequestDomainModel): GraphQlExtracted? {
    val request = decoded.body?.let {
        try {
            val requestBody = graphQlParser.decodeFromString<GraphQlRequestBody>(it)

            val queryName = extractOperationName(requestBody.query)
            val operationType = extractOperationType(requestBody.query)

            GraphQlExtracted.Request(
                requestBody = requestBody,
                queryName = queryName,
                operationType = operationType ?: return null,
            )
        } catch (t: Throwable) {
            null
        }
    }

    return if (request != null) {
        GraphQlExtracted(
            request = request,
        )
    } else null
}

fun extractOperationType(query: String): String? {
    val regex = Regex("""\b(query|mutation|subscription)\b""")
    val matchResult = regex.find(query.trim())
    return matchResult?.value
}

private fun extractOperationName(query: String): String? {
    val regex = Regex("""\b(query|mutation|subscription)\s+(\w+)""")
    val matchResult = regex.find(query.trim())
    return matchResult?.groups?.get(2)?.value
}

fun computeIsGraphQlSuccess(
    responseHttpCode: Int,
    response: GraphQlResponseBody?,
): Boolean {
    if (responseHttpCode !in 200..299) return false
    return response?.errors?.takeUnless { it.isEmpty() } == null
}
