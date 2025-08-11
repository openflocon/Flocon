package io.github.openflocon.flocon.okhttp

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.plugins.network.FloconNetworkPlugin
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest
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

class FloconOkhttpInterceptor() : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val floconNetworkPlugin = FloconApp.instance?.client?.networkPlugin
        if (floconNetworkPlugin == null) {
            // on no op, do not intercept the call, just execute it
            return chain.proceed(chain.request())
        }

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

        val startTime = System.nanoTime()

        var isMocked = false
        val response = tryToMock(
            request = request,
            floconNetworkPlugin = floconNetworkPlugin,
        )?.also {
            isMocked = true
        } ?: chain.proceed(request)

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
            responseBodyString = response.peekBody(Long.MAX_VALUE).string() // Reads the body safely
            responseSize = responseBody.contentLength().let {
                if (it != -1L) it else responseBodyString?.toByteArray(StandardCharsets.UTF_8)?.size?.toLong()
            }
        }
        val requestHeadersMap =
            request.headers.toMultimap().mapValues { it.value.joinToString(",") }
        val responseHeadersMap =
            response.headers.toMultimap().mapValues { it.value.joinToString(",") }

        val isImage = responseContentType?.toString()?.startsWith("image/") == true

        val floconRequest = FloconNetworkRequest(
            durationMs = durationMs,
            floconNetworkType = "http",
            request = FloconNetworkRequest.Request(
                url = request.url.toString(),
                method = request.method,
                startTime = requestedAt,
                headers = requestHeadersMap,
                body = requestBodyString,
                size = requestSize,
            ),
            response = FloconNetworkRequest.Response(
                httpCode = response.code,
                contentType = responseContentType?.toString(),
                body = responseBodyString.takeUnless { isImage }, // dont send images responses bytes
                headers = responseHeadersMap,
                size = responseSize,
                grpcStatus = null,
            ),
            isMocked = isMocked,
        )

        floconNetworkPlugin.log(floconRequest)

        // Rebuild the response with a new body so that the chain can continue
        // The original response body is already consumed by peekBody, so no need to rebuild with it.
        // Just return the original response if you don't modify the body itself.
        return response
    }

    private fun tryToMock(
        request: Request,
        floconNetworkPlugin: FloconNetworkPlugin,
    ): Response? {
        for (mock in floconNetworkPlugin.mocks) {
            val url = request.url.toString()
            val method = request.method

            val urlMatches = mock.expectation.pattern.matcher(url).matches()
            val methodMatches = mock.expectation.method == "*" || mock.expectation.method.equals(
                method,
                ignoreCase = true
            )

            if (urlMatches && methodMatches) {
                FloconLogger.log("Request $url mocked with HTTP code ${mock.response.httpCode}")

                // Applique le délai si nécessaire
                if (mock.response.delay > 0) {
                    try {
                        Thread.sleep(mock.response.delay)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                }

                val body = mock.response.body.toResponseBody(
                    mock.response.mediaType.toMediaTypeOrNull()
                )

                return Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .message(getHttpMessage(mock.response.httpCode))
                    .code(mock.response.httpCode)
                    .body(body)
                    .apply {
                        mock.response.headers.forEach { (name, value) ->
                            addHeader(name, value)
                        }
                    }
                    .build()
            }
        }
        return null
    }
}