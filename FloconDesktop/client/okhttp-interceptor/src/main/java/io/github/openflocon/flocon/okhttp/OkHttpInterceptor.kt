package io.github.openflocon.flocon.okhttp

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.nio.charset.StandardCharsets

class FloconOkhttpInterceptor(
    private val floconClient: Flocon.Client? = null,
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
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
        val response = chain.proceed(request)
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
        val requestHeadersMap = request.headers.toMultimap().mapValues { it.value.joinToString(",") }
        val responseHeadersMap = response.headers.toMultimap().mapValues { it.value.joinToString(",") }

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
            )
        )

        val json = floconRequest.toJson()
        (floconClient ?: Flocon.client)?.send( // Use Flocon.client directly
            plugin = Protocol.FromDevice.Network.Plugin,
            method = Protocol.FromDevice.Network.Method.LogNetworkCall,
            body = json.toString(),
        )

        // Rebuild the response with a new body so that the chain can continue
        // The original response body is already consumed by peekBody, so no need to rebuild with it.
        // Just return the original response if you don't modify the body itself.
        return response
    }
}