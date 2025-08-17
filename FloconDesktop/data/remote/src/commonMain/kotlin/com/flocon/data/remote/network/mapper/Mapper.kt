package com.flocon.data.remote.network.mapper

import com.flocon.data.remote.network.models.BadQualityConfigDataModel
import com.flocon.data.remote.network.models.FloconNetworkRequestDataModel
import com.flocon.data.remote.network.models.MockNetworkResponseDataModel
import io.github.openflocon.data.core.network.graphql.model.GraphQlExtracted
import io.github.openflocon.data.core.network.graphql.model.GraphQlRequestBody
import io.github.openflocon.data.core.network.graphql.model.GraphQlResponseBody
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkRequestDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi

fun listToRemote(mocks: List<MockNetworkDomainModel>): List<MockNetworkResponseDataModel> =
    mocks.map { toRemote(it) }

fun toRemote(mock: MockNetworkDomainModel): MockNetworkResponseDataModel =
    MockNetworkResponseDataModel(
        expectation = MockNetworkResponseDataModel.Expectation(
            urlPattern = mock.expectation.urlPattern,
            method = mock.expectation.method,
        ),
        response = MockNetworkResponseDataModel.Response(
            httpCode = mock.response.httpCode,
            body = mock.response.body,
            mediaType = mock.response.mediaType,
            delay = mock.response.delay,
            headers = mock.response.headers,
        ),
    )

@OptIn(ExperimentalUuidApi::class)
fun toDomain(decoded: FloconNetworkRequestDataModel): FloconNetworkCallDomainModel? = try {
    val graphQl = extractGraphQl(decoded)

    val callId = decoded.floconCallId!!
    val networkRequest = FloconNetworkRequestDomainModel(
        url = decoded.url!!,
        startTime = decoded.startTime!!,
        method = decoded.method!!,
        headers = decoded.requestHeaders!!,
        body = decoded.requestBody,
        byteSize = decoded.requestSize ?: 0L,
        isMocked = decoded.isMocked ?: false,
    )

    when {
        graphQl != null -> FloconNetworkCallDomainModel.GraphQl(
            callId = callId,
            request = FloconNetworkCallDomainModel.GraphQl.Request(
                query = graphQl.request.queryName ?: "anonymous",
                operationType = graphQl.request.operationType,
                networkRequest = networkRequest,
            ),
            response = null,
        )

        decoded.floconNetworkType == "grpc" -> FloconNetworkCallDomainModel.Grpc(
            callId = callId,
            networkRequest = networkRequest,
            response = null,
        )
        // decoded.floconNetworkType == "http"
        else -> {
            FloconNetworkCallDomainModel.Http(
                callId = callId,
                networkRequest = networkRequest,
                response = null,
            )
        }
    }
} catch (t: Throwable) {
    t.printStackTrace()
    null
}


private val graphQlParser = Json {
    ignoreUnknownKeys = true
}

// maybe use graphql-java
fun extractGraphQl(decoded: FloconNetworkRequestDataModel): GraphQlExtracted? {
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

fun toRemote(domain: BadQualityConfigDomainModel): BadQualityConfigDataModel =
    BadQualityConfigDataModel(
        latency = with(domain.latency) {
            BadQualityConfigDataModel.LatencyConfig(
                latencyTriggerProbability = triggerProbability,
                minLatencyMs = minLatencyMs,
                maxLatencyMs = maxLatencyMs,
            )
        },
        errorProbability = domain.errorProbability,
        errors = domain.errors.map {
            BadQualityConfigDataModel.Error(
                weight = it.weight,
                errorCode = it.httpCode,
                errorBody = it.body,
                errorContentType = it.contentType,
            )
        },
    )
