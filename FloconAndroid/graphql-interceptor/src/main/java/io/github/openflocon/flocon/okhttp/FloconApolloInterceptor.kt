package io.github.openflocon.flocon.okhttp

import com.apollographql.apollo.api.http.ByteStringHttpBody
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.okhttp.model.FloconGrpcRequest
import com.apollographql.apollo.api.http.HttpMethod
import com.apollographql.apollo.api.http.HttpRequest
import com.apollographql.apollo.api.http.HttpResponse
import com.apollographql.apollo.network.http.HttpInterceptor
import com.apollographql.apollo.network.http.HttpInterceptorChain
import okio.Buffer
import java.nio.charset.StandardCharsets

class FloconApolloInterceptor(
    private val floconClient: Flocon.Client? = null,
) : HttpInterceptor {

    override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {
        val requestedAt = System.currentTimeMillis()

        val requestHeadersMap = request.headers.associate { it.name to it.value }

        val startTime = System.nanoTime()

        var requestBodyString: String? = null

        val requestBody = request.body
        val newRequest = if (requestBody == null) {
            request
        } else {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val bodyByteString = buffer.readByteString()
            requestBodyString = bodyByteString.utf8()
            request.newBuilder()
                .body(ByteStringHttpBody(contentType = requestBody.contentType, bodyByteString))
                .build()
        }
        val requestSize = requestBodyString?.toByteArray(StandardCharsets.UTF_8)?.size?.toLong()

        val httpResponse = chain.proceed(newRequest)
        val endTime = System.nanoTime()

        val durationMs = (endTime - startTime) / 1e6

        val responseBody = httpResponse.body
        val bodyByteString = responseBody?.readByteString()
        val responseBodyString = bodyByteString?.utf8()
        val responseSize = responseBodyString?.toByteArray(StandardCharsets.UTF_8)?.size?.toLong()
        val responseHeadersMap = httpResponse.headers.associate { it.name to it.value }

        val isImage = httpResponse.headers.firstOrNull { it.name.equals("Content-Type", ignoreCase = true) }?.value?.startsWith("image/") == true

        val floconRequest = FloconGrpcRequest(
            url = request.url,
            method = when (request.method) {
                HttpMethod.Get -> "GET"
                HttpMethod.Post -> "POST"
            },
            startTime = requestedAt,
            durationMs = durationMs,
            requestHeaders = requestHeadersMap,
            requestBody = requestBodyString,
            requestSize = requestSize,
            responseHttpCode = httpResponse.statusCode,
            responseContentType = httpResponse.headers.firstOrNull { it.name.equals("Content-Type", ignoreCase = true) }?.value,
            responseBody = responseBodyString.takeUnless { isImage },
            responseHeaders = responseHeadersMap,
            responseSize = responseSize,
        )

        val json = floconRequest.toJson()
        (floconClient ?: Flocon.client)?.send(
            plugin = Protocol.FromDevice.GraphQl.Plugin,
            method = Protocol.FromDevice.GraphQl.Method.LogNetworkCall,
            body = json.toString(),
        )

        return if (responseBody == null || bodyByteString == null) {
            httpResponse
        } else {
            @Suppress("DEPRECATION")
            HttpResponse.Builder(statusCode = httpResponse.statusCode)
                .body(bodyByteString)
                .addHeaders(httpResponse.headers)
                .build()
        }
    }

}