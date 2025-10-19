package io.github.openflocon.flocon.ktor

import io.github.openflocon.flocon.plugins.network.FloconNetworkPlugin
import io.github.openflocon.flocon.plugins.network.model.MockNetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.delay
import java.io.IOException
import kotlin.collections.component1
import kotlin.collections.component2

internal fun findMock(
    request: HttpRequestBuilder,
    floconNetworkPlugin: FloconNetworkPlugin,
): MockNetworkResponse? {
    val url =  request.url.toString()
    val method = request.method.value
    return floconNetworkPlugin.mocks.firstOrNull {
        it.expectation.matches(
            url = url,
            method = method
        )
    }
}

@Throws(IOException::class)
@OptIn(InternalAPI::class)
internal suspend fun executeMock(
    request: HttpRequestBuilder,
    client: HttpClient,
    mock: MockNetworkResponse
): HttpClientCall {
    if (mock.response.delay > 0) {
        delay(mock.response.delay)
    }

    when(val response = mock.response) {
        is MockNetworkResponse.Response.Body -> {
            val bodyBytes = response.body.toByteArray()
            val headers = HeadersBuilder().apply {
                response.headers.forEach { (name, value) -> append(name, value) }
            }.build()

            val responseData = HttpResponseData(
                statusCode = HttpStatusCode.fromValue(response.httpCode),
                requestTime = GMTDate(),
                headers = headers,
                version = HttpProtocolVersion.HTTP_1_1,
                body = ByteReadChannel(bodyBytes),
                callContext = client.coroutineContext,
            )

            return HttpClientCall(client, request.build(), responseData)
        }
        is MockNetworkResponse.Response.ErrorThrow -> {
            val error = response.generate()
            if (error is IOException) {
                throw error //okhttp accepts only IOException
            } else if (error is Throwable) {
                throw IOException(error)
            } else {
                throw IOException("Unknown flocon/mock error type")
            }
        }
    }
}