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
import kotlin.collections.component1
import kotlin.collections.component2

internal fun findMock(
    request: HttpRequestBuilder,
    plugin: FloconNetworkPlugin,
): MockNetworkResponse? {
    for (mock in plugin.mocks) {
        val urlMatches = mock.expectation.pattern.matcher(request.url.toString()).matches()
        val methodMatches = mock.expectation.method == "*" ||
                mock.expectation.method.equals(request.method.value, ignoreCase = true)
        if (urlMatches && methodMatches) return mock
    }
    return null
}

@OptIn(InternalAPI::class)
internal suspend fun executeMock(
    request: HttpRequestBuilder,
    client: HttpClient,
    mock: MockNetworkResponse
): HttpClientCall {
    if (mock.response.delay > 0) {
        delay(mock.response.delay)
    }

    val bodyBytes = mock.response.body.toByteArray()
    val headers = HeadersBuilder().apply {
        mock.response.headers.forEach { (name, value) -> append(name, value) }
    }.build()

    val responseData = HttpResponseData(
        statusCode = HttpStatusCode.fromValue(mock.response.httpCode),
        requestTime = GMTDate(),
        headers = headers,
        version = HttpProtocolVersion.HTTP_1_1,
        body = ByteReadChannel(bodyBytes),
        callContext = client.coroutineContext,
    )

    return HttpClientCall(client, request.build(), responseData)
}