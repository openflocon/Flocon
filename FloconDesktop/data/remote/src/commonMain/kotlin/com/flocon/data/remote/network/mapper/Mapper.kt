package com.flocon.data.remote.network.mapper

import com.flocon.data.remote.network.models.BadQualityConfigDataModel
import com.flocon.data.remote.network.models.FloconNetworkRequestDataModel
import com.flocon.data.remote.network.models.MockNetworkResponseDataModel
import io.github.openflocon.data.core.network.graphql.model.GraphQlExtracted
import io.github.openflocon.data.core.network.graphql.model.GraphQlRequestBody
import io.github.openflocon.data.core.network.graphql.model.GraphQlResponseBody
import io.github.openflocon.domain.device.models.AppInstance
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi

fun listToRemote(mocks: List<MockNetworkDomainModel>): List<MockNetworkResponseDataModel> = mocks.map { toRemote(it) }

fun toRemote(mock: MockNetworkDomainModel): MockNetworkResponseDataModel = MockNetworkResponseDataModel(
    expectation = MockNetworkResponseDataModel.Expectation(
        urlPattern = mock.expectation.urlPattern,
        method = mock.expectation.method,
    ),
    response = when (val r = mock.response) {
        is MockNetworkDomainModel.Response.Body -> MockNetworkResponseDataModel.Response(
            httpCode = r.httpCode,
            body = r.body,
            mediaType = r.mediaType,
            delay = r.delay,
            headers = r.headers,
            errorException = null,
        )
        is MockNetworkDomainModel.Response.Exception -> MockNetworkResponseDataModel.Response(
            httpCode = null,
            body = null,
            mediaType = null,
            delay = r.delay,
            headers = null,
            errorException = r.classPath,
        )
    },
)

@OptIn(ExperimentalUuidApi::class)
fun toDomain(
    decoded: FloconNetworkRequestDataModel,
    appInstance: AppInstance,
): FloconNetworkCallDomainModel? = try {
    val graphQl = extractGraphQl(decoded)

    val callId = decoded.floconCallId!!

    val request = FloconNetworkCallDomainModel.Request(
        url = decoded.url!!,
        startTime = decoded.startTime!!,
        method = decoded.method!!,
        headers = decoded.requestHeaders!!,
        body = decoded.requestBody,
        byteSize = decoded.requestSize ?: 0L,
        isMocked = decoded.isMocked ?: false,
        specificInfos = when {
            graphQl != null -> FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl(
                query = graphQl.request.requestBody.query,
                operationType = graphQl.request.operationType,
            )
            decoded.floconNetworkType == "grpc" -> FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc
            else -> FloconNetworkCallDomainModel.Request.SpecificInfos.Http
        },
    )

    FloconNetworkCallDomainModel(
        callId = callId,
        appInstance = appInstance,
        request = request,
        response = null, // for now it's null
    )
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

fun toRemote(domain: BadQualityConfigDomainModel): BadQualityConfigDataModel = BadQualityConfigDataModel(
    latency = with(domain.latency) {
        BadQualityConfigDataModel.LatencyConfig(
            latencyTriggerProbability = triggerProbability,
            minLatencyMs = minLatencyMs,
            maxLatencyMs = maxLatencyMs,
        )
    },
    errorProbability = domain.errorProbability,
    errors = domain.errors.map {
        when (val t = it.type) {
            is BadQualityConfigDomainModel.Error.Type.Body -> BadQualityConfigDataModel.Error(
                weight = it.weight,
                errorCode = t.httpCode,
                errorBody = t.body,
                errorContentType = t.contentType,
                errorException = null,
            )
            is BadQualityConfigDomainModel.Error.Type.Exception -> BadQualityConfigDataModel.Error(
                weight = it.weight,
                errorException = t.classPath,
                errorCode = null,
                errorBody = null,
                errorContentType = null,
            )
        }
    },
)
