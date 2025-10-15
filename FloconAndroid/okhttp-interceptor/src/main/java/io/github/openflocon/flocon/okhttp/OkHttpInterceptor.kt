package io.github.openflocon.flocon.okhttp

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkResponse
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.UUID

data class FloconNetworkIsImageParams(
    val request: Request,
    val response: Response,
    val responseContentType: String?,
)

class FloconOkhttpInterceptor(
    private val isImage: ((FloconNetworkIsImageParams) -> Boolean)? = null,
) : Interceptor {

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

        val requestHeadersMap =
            request.headers.toMultimap().mapValues { it.value.joinToString(",") }

        if (requestBody != null) {
            val (rBodyString, rSize) = extractRequestBodyInfo(
                request = request,
                requestHeaders = requestHeadersMap,
            )
            requestBodyString = rBodyString
            requestSize = rSize
        }

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

            val responseHeadersMap =
                response.headers.toMultimap().mapValues { it.value.joinToString(",") }

            if (responseBody != null) {
                val (rBodyString, rSize) = extractResponseBodyInfo(
                    response = response,
                    responseHeaders = responseHeadersMap,
                )
                responseBodyString = rBodyString
                responseSize = rSize
            }

            val isImage =
                responseContentType?.toString()?.startsWith("image/") == true || (isImage?.invoke(
                    FloconNetworkIsImageParams(
                        request = request,
                        response = response,
                        responseContentType = responseContentType?.toString(),
                    )
                ) == true)

            val requestHeadersMapUpToDate =
                response.request.headers.toMultimap().mapValues { it.value.joinToString(",") }

            val floconCallResponse = FloconNetworkResponse(
                httpCode = response.code,
                contentType = responseContentType?.toString(),
                body = responseBodyString.takeUnless { isImage }, // dont send images responses bytes
                headers = responseHeadersMap,
                size = responseSize,
                grpcStatus = null,
                error = null,
                requestHeaders = requestHeadersMapUpToDate,
                isImage = isImage,
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
                requestHeaders = null,
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
            throw e
        }
    }

}

internal fun MediaType?.charsetOrUtf8(): Charset = this?.charset() ?: Charsets.UTF_8

private fun extractResponseBodyInfo(
    response: Response,
    responseHeaders: Map<String, String>
): Pair<String?, Long?> {
    val responseBody = response.body ?: return null to null

    var bodyString: String? = null
    var bodySize: Long? = null

    val source = responseBody.source()
    source.request(Long.MAX_VALUE) // Buffer the entire body, otherwise we have an empty string

    var buffer = source.buffer

    val charset = responseBody.contentType().charsetOrUtf8()

    val contentEncoding = responseHeaders["Content-Encoding"] ?: responseHeaders["content-encoding"]
    if ("gzip".equals(contentEncoding, ignoreCase = true)) {
        GzipSource(buffer.clone()).use { gzippedResponseBody ->
            buffer = Buffer()
            buffer.writeAll(gzippedResponseBody)
        }

        bodyString = buffer.clone().readString(charset)
        bodySize = buffer.size
    } else {
        bodyString = buffer.clone().readString(charset)
        bodySize = buffer.size
    }

    return bodyString to bodySize
}

private fun extractRequestBodyInfo(
    request: Request,
    requestHeaders: Map<String, String>
): Pair<String?, Long?> {
    val requestBody = request.body ?: return null to null

    var bodySize: Long? = null

    var buffer = Buffer()
    requestBody.writeTo(buffer)

    val contentEncoding = requestHeaders["Content-Encoding"] ?: requestHeaders["content-encoding"]

    if ("gzip".equals(contentEncoding, ignoreCase = true)) {
        bodySize = buffer.size
        GzipSource(buffer).use { gzippedResponseBody ->
            buffer = Buffer()
            buffer.writeAll(gzippedResponseBody)
        }
    }

    val charset = requestBody.contentType().charsetOrUtf8()
    val bodyString = buffer.readString(charset)

    return bodyString to bodySize
}