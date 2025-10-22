@file:OptIn(ExperimentalUuidApi::class)

package io.github.openflocon.flocon.ktor

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkResponse
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentType
import io.ktor.util.AttributeKey
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.toByteArray
import io.github.openflocon.flocon.utils.currentTimeMillis
import io.github.openflocon.flocon.utils.currentTimeNanos
import io.ktor.client.call.save
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


data class FloconNetworkIsImageParams(
    val request: HttpRequest,
    val response: HttpResponse,
    val responseContentType: String?,
)

class FloconKtorPluginConfig {
    var isImage: ((param: FloconNetworkIsImageParams) -> Boolean)? = null
}

val FloconKtorPlugin = createClientPlugin("FloconKtorPlugin", ::FloconKtorPluginConfig) {

    val theClient = client
    val isImageCallback = pluginConfig.isImage

    // Intercept requests
    client.sendPipeline.intercept(HttpSendPipeline.Monitoring) {
        val floconNetworkPlugin = FloconApp.instance?.client?.networkPlugin
        if (floconNetworkPlugin == null) {
            proceed()
            return@intercept
        }

        val request = context
        val floconCallId = Uuid.random().toString()
        val floconNetworkType = "http"
        val requestedAt = currentTimeMillis()

        // Reads the body without consuming it
        val requestBodyString = extractAndReplaceRequestBody(request)
        val requestSize = requestBodyString?.encodeToByteArray()?.size?.toLong()
        val requestHeadersMap =
            request.headers.entries().associate { it.key to it.value.joinToString(",") }

        val mockConfig = findMock(request, floconNetworkPlugin)
        val isMocked = mockConfig != null

        val floconNetworkRequest = FloconNetworkRequest(
            url = request.url.toString(),
            method = request.method.value,
            startTime = requestedAt,
            headers = requestHeadersMap,
            body = requestBodyString,
            size = requestSize,
            isMocked = isMocked
        )

        floconNetworkPlugin.logRequest(
            FloconNetworkCallRequest(
                floconCallId = floconCallId,
                floconNetworkType = floconNetworkType,
                isMocked = isMocked,
                request = floconNetworkRequest
            )
        )

        val startTime = currentTimeNanos()
        request.attributes.put(FLOCON_CALL_ID_KEY, floconCallId)
        request.attributes.put(FLOCON_START_TIME_KEY, startTime)
        request.attributes.put(FLOCON_IS_MOCKED_KEY, isMocked)

        try {
            if (isMocked) {
                val fakeCall = executeMock(client = theClient, request = request, mock = mockConfig)
                proceedWith(fakeCall)
                return@intercept
            }

            floconNetworkPlugin.badQualityConfig?.let { badQualityConfig ->
                executeBadQuality(
                    badQualityConfig = badQualityConfig,
                    client = theClient,
                    request = request
                )
            } ?: run {
                proceed()
            }
        } catch (t: Throwable) {
            val endTime = currentTimeNanos()

            val durationMs: Double = (endTime - startTime) / 1e6

            val floconCallResponse = FloconNetworkResponse(
                httpCode = null,
                contentType = null,
                body = null,
                headers = emptyMap(),
                size = null,
                grpcStatus = null,
                error = t.message ?: t::class.simpleName ?: "Unknown",
                requestHeaders = requestHeadersMap,
                isImage = false,
            )

            floconNetworkPlugin.logResponse(
                FloconNetworkCallResponse(
                    floconCallId = floconCallId,
                    durationMs = durationMs,
                    floconNetworkType = floconNetworkType,
                    isMocked = isMocked,
                    response = floconCallResponse,
                )
            )
            throw t
        }
    }

    // Intercepts responses
    client.receivePipeline.intercept(HttpReceivePipeline.After) { response ->
        val floconNetworkPlugin = FloconApp.instance?.client?.networkPlugin ?: return@intercept

        val savedCall = response.call.save()
        val savedResponse = savedCall.response
        val call = response.call
        val request: HttpRequest = call.request

        val floconCallId = request.attributes.getOrNull(FLOCON_CALL_ID_KEY) ?: return@intercept
        val startTime = request.attributes.getOrNull(FLOCON_START_TIME_KEY) ?: return@intercept
        val isMocked = request.attributes.getOrNull(FLOCON_IS_MOCKED_KEY) ?: false

        val endTime = currentTimeNanos()
        val durationMs = (endTime - startTime) / 1e6

        val originalBodyBytes = response.bodyAsChannel().toByteArray()
        val responseSize = originalBodyBytes.size.toLong()

        val responseHeadersMap =
            response.headers.entries().associate { it.key to it.value.joinToString(",") }
        val contentType = response.contentType()?.toString()

        val requestHeadersMap =
            request.headers.entries().associate { it.key to it.value.joinToString(",") }

        val isImage = contentType?.startsWith("image/") == true || isImageCallback?.invoke(
            FloconNetworkIsImageParams(
                request = request,
                response = response,
                responseContentType = contentType,
            )
        ) == true

        val floconCallResponse = FloconNetworkResponse(
            httpCode = response.status.value,
            contentType = contentType,
            body = if(isImage) null else originalBodyBytes.decodeToString(),
            headers = responseHeadersMap,
            size = responseSize,
            grpcStatus = null,
            error = null,
            requestHeaders = requestHeadersMap,
            isImage = isImage,
        )

        floconNetworkPlugin.logResponse(
            FloconNetworkCallResponse(
                floconCallId = floconCallId,
                durationMs = durationMs,
                floconNetworkType = "http",
                isMocked = isMocked,
                response = floconCallResponse
            )
        )

        proceedWith(savedResponse)
    }
}

fun HttpClientConfig<*>.floconInterceptor() {
    install(FloconKtorPlugin)
}

private val FLOCON_CALL_ID_KEY = AttributeKey<String>("floconCallId")
private val FLOCON_START_TIME_KEY = AttributeKey<Long>("floconStartTime")
private val FLOCON_IS_MOCKED_KEY = AttributeKey<Boolean>("floconIsMocked")