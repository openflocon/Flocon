package io.github.openflocon.flocon.okhttp

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.network.FloconNetworkPlugin
import io.github.openflocon.flocon.plugins.network.model.BadQualityConfig
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkResponse
import io.github.openflocon.flocon.plugins.network.model.MockNetworkResponse
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.UUID
import kotlin.random.Random

class FloconOkhttpInterceptor() : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val floconNetworkPlugin = FloconApp.instance?.client?.networkPlugin
        if (floconNetworkPlugin == null) {
            // on no op, do not intercept the call, just execute it
            return chain.proceed(chain.request())
        }

        val floconCallId = UUID.randomUUID().toString()
        val floconNetworkType = "http"

        val request = chain.request()

        val requestedAt = System.currentTimeMillis()

        val requestBody = request.body
        var requestBodyString: String? = null
        var requestSize: Long? = null
        if (requestBody != null) {
            val contentType: MediaType? = requestBody.contentType()
            var charset = StandardCharsets.UTF_8
            if (contentType != null) {
                charset = contentType.charset(StandardCharsets.UTF_8)
            }
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            requestBodyString = buffer.readString(charset!!)
            requestSize = requestBody.contentLength().let {
                if (it != -1L) it else requestBodyString?.toByteArray(charset)?.size?.toLong()
            }
        }
        val requestHeadersMap =
            request.headers.toMultimap().mapValues { it.value.joinToString(",") }

        val startTime = System.nanoTime()

        val mockConfig = findMock(
            request = request,
            floconNetworkPlugin = floconNetworkPlugin,
        )
        val isMocked = mockConfig != null

        val floconNetworkRequest = FloconNetworkRequest(
            url = request.url.toString(),
            method = request.method,
            startTime = requestedAt,
            headers = requestHeadersMap,
            body = requestBodyString,
            size = requestSize,
            isMocked = isMocked,
        )

        floconNetworkPlugin.logRequest(
            FloconNetworkCallRequest(
                floconCallId = floconCallId,
                floconNetworkType = floconNetworkType,
                isMocked = isMocked,
                request = floconNetworkRequest,
            )
        )

        try {
            val response = if (isMocked) {
                executeMock(request = request, mock = mockConfig)
            } else {
                floconNetworkPlugin.badQualityConfig?.let { badQualityConfig ->
                    executeBadQuality(
                        badQualityConfig = badQualityConfig,
                        request = request,
                    )
                } ?: run {
                    chain.proceed(request)
                }
            }

            val endTime = System.nanoTime()

            val durationMs: Double = (endTime - startTime) / 1e6

            // To get the response body, be careful
            // because the body can only be read once.
            // It must be duplicated so that the chain can continue normally.
            val responseBody = response.body
            var responseBodyString: String? = null
            var responseSize: Long? = null
            val responseContentType: MediaType? = responseBody?.contentType()

            if (responseBody != null) {
                // Use response.peekBody() to safely read the body without consuming it
                // Note: peekBody has a max size, adjust as needed.
                responseBodyString =
                    response.peekBody(Long.MAX_VALUE).string() // Reads the body safely
                responseSize = responseBody.contentLength().let {
                    if (it != -1L) it else responseBodyString?.toByteArray(StandardCharsets.UTF_8)?.size?.toLong()
                }
            }
            val responseHeadersMap =
                response.headers.toMultimap().mapValues { it.value.joinToString(",") }

            val isImage = responseContentType?.toString()?.startsWith("image/") == true

            val floconCallResponse = FloconNetworkResponse(
                httpCode = response.code,
                contentType = responseContentType?.toString(),
                body = responseBodyString.takeUnless { isImage }, // dont send images responses bytes
                headers = responseHeadersMap,
                size = responseSize,
                grpcStatus = null,
                error = null,
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

            // Rebuild the response with a new body so that the chain can continue
            // The original response body is already consumed by peekBody, so no need to rebuild with it.
            // Just return the original response if you don't modify the body itself.
            return response
        } catch (e: IOException) {
            val endTime = System.nanoTime()

            val durationMs: Double = (endTime - startTime) / 1e6

            val floconCallResponse = FloconNetworkResponse(
                httpCode = null,
                contentType = null,
                body = null,
                headers = emptyMap(),
                size = null,
                grpcStatus = null,
                error = e.message ?: e.javaClass.simpleName,
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
            throw e
        }
    }

}