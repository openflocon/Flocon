package io.github.openflocon.flocondesktop.features.network.data.parser.graphql

import io.github.openflocon.flocondesktop.features.network.data.model.FloconHttpRequestDataModel
import io.github.openflocon.flocondesktop.features.network.data.parser.graphql.model.GraphQlRequestBody
import io.github.openflocon.flocondesktop.features.network.data.parser.graphql.model.GraphQlResponseBody
import io.github.openflocon.flocondesktop.features.network.data.parser.graphql.model.GraphQlExtracted
import kotlinx.serialization.json.Json

private val graphQlParser =
    Json {
        ignoreUnknownKeys = true
    }

fun extractGraphQl(decoded: FloconHttpRequestDataModel) : GraphQlExtracted? {
    val request = decoded.requestBody?.let {
        try {
            val requestBody = graphQlParser.decodeFromString<GraphQlRequestBody>(it)

            val queryName = extractOperationName(requestBody.query)

            GraphQlExtracted.Request(
                requestBody = requestBody,
                queryName = queryName,
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

    return if(request != null && response != null) {
        GraphQlExtracted(
            request = request,
            response = response,
        )
    } else null
}

private fun extractOperationName(query: String): String? {
    val regex = Regex("""\b(query|mutation|subscription)\s+(\w+)""")
    val matchResult = regex.find(query.trim())
    return matchResult?.groups?.get(2)?.value
}
