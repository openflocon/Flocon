package io.github.openflocon.flocondesktop.features.network.data.parser.graphql

import io.github.openflocon.flocondesktop.features.network.data.model.FloconHttpRequestDataModel
import io.github.openflocon.flocondesktop.features.network.data.parser.graphql.model.GraphQlExtracted
import io.github.openflocon.flocondesktop.features.network.data.parser.graphql.model.GraphQlRequestBody
import io.github.openflocon.flocondesktop.features.network.data.parser.graphql.model.GraphQlResponseBody
import kotlinx.serialization.json.Json

private val graphQlParser =
    Json {
        ignoreUnknownKeys = true
    }

// maybe use graphql-java
fun extractGraphQl(decoded: FloconHttpRequestDataModel): GraphQlExtracted? {
    val request = decoded.requestBody?.let {
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
    val response = decoded.responseBody?.let {
        try {
            graphQlParser.decodeFromString<GraphQlResponseBody>(it)
        } catch (t: Throwable) {
            null
        }
    }

    return if (request != null && response != null) {
        GraphQlExtracted(
            request = request,
            response = response,
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
