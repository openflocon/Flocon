package com.flocon.data.remote.network.mapper

import com.flocon.data.remote.network.mapper.extractDomain
import com.flocon.data.remote.network.models.BadQualityConfigDataModel
import com.flocon.data.remote.network.models.FloconNetworkRequestDataModel
import com.flocon.data.remote.network.models.FloconNetworkWebSocketEvent
import com.flocon.data.remote.network.models.MockNetworkResponseDataModel
import io.github.openflocon.data.core.network.graphql.model.GraphQlExtracted
import io.github.openflocon.data.core.network.graphql.model.GraphQlRequestBody
import io.github.openflocon.domain.common.ByteFormatter
import io.github.openflocon.domain.common.time.formatTimestamp
import io.github.openflocon.domain.device.models.AppInstance
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.ktor.server.util.url
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.URLDecoder
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

    val requestSize = decoded.requestSize ?: 0L
    val startTime = decoded.startTime!!

    val specificInfos = when {
        graphQl != null -> when (graphQl) {
            is GraphQlExtracted.WithBody -> FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl(
                query = graphQl.requestBody.query,
                operationType = graphQl.operationType,
            )
            is GraphQlExtracted.PersistedQuery -> FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl(
                query = graphQl.queryName,
                operationType = graphQl.operationType,
            )
        }

        decoded.floconNetworkType == "grpc" -> FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc
        else -> FloconNetworkCallDomainModel.Request.SpecificInfos.Http
    }

    val request = FloconNetworkCallDomainModel.Request(
        url = decoded.url!!,
        startTime = startTime,
        startTimeFormatted = formatTimestamp(startTime),
        method = decoded.method!!,
        headers = decoded.requestHeaders!!,
        body = decoded.requestBody,
        byteSize = requestSize,
        byteSizeFormatted = ByteFormatter.formatBytes(requestSize),
        isMocked = decoded.isMocked ?: false,
        specificInfos = specificInfos,
        domainFormatted = extractDomain(
            requestUrl = decoded.url,
            specificInfos = specificInfos,
        ),
        methodFormatted = extractMethod(
            requestMethod = decoded.method,
            specificInfos = specificInfos,
        ),
        queryFormatted = extractQueryFormatted(
            requestUrl = decoded.url,
            requestMethod = decoded.method,
            specificInfos = specificInfos,
        ),
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
    decoded.requestBody?.let {
        try {
            val requestBody = graphQlParser.decodeFromString<GraphQlRequestBody>(it)

            val queryName = extractOperationName(requestBody.query)
            val operationType = extractOperationType(requestBody.query)

            return GraphQlExtracted.WithBody(
                requestBody = requestBody,
                queryName = queryName,
                operationType = operationType ?: return null,
            )
        } catch (t: Throwable) {
            null
        }
    }

    // case with query params (persisted query)
    decoded.url?.let { urlString ->
        try {
            val uri = URI(urlString)
            val queryParams = uri.query
                ?.split("&")
                ?.associate {
                    val (k, v) = it.split("=")
                    k to URLDecoder.decode(v, "UTF-8")
                } ?: emptyMap()

            val queryName = queryParams["operationName"]
            val extensions = queryParams["extensions"]

            if (queryName != null && extensions?.contains("persistedQuery") == true) {
                return GraphQlExtracted.PersistedQuery(
                    queryName = queryName,
                    operationType = "persistedQuery",
                )
            } else null
        } catch (t: Throwable) {
            null
        }
    }

    return null
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

@OptIn(ExperimentalUuidApi::class)
fun FloconNetworkWebSocketEvent.toDomain(
    appInstance: AppInstance,
): FloconNetworkCallDomainModel? = try {
    val callId = id!!
    val startTime = timestamp!!

    val specificInfos = FloconNetworkCallDomainModel.Request.SpecificInfos.WebSocket(
        event = event!!,
    )

    val method = "websocket"
    val body = message ?: error ?: event
    val size = size ?: 0L

    val request = FloconNetworkCallDomainModel.Request(
        url = url!!,
        startTime = startTime,
        startTimeFormatted = formatTimestamp(startTime),
        method = method,
        headers = emptyMap(),
        body = body,
        byteSize = size,
        byteSizeFormatted = ByteFormatter.formatBytes(size),
        isMocked = false, // TODO ?
        specificInfos = specificInfos,
        domainFormatted = extractDomain(url, specificInfos),
        methodFormatted = method,
        queryFormatted = body,
    )

    FloconNetworkCallDomainModel(
        callId = callId,
        appInstance = appInstance,
        request = request,
        response = null, // no response for websocket
    )
} catch (t: Throwable) {
    t.printStackTrace()
    null
}
