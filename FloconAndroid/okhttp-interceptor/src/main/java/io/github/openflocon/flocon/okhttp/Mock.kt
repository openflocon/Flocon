package io.github.openflocon.flocon.okhttp

import io.github.openflocon.flocon.plugins.network.FloconNetworkPlugin
import io.github.openflocon.flocon.plugins.network.model.MockNetworkResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

internal fun findMock(
    request: Request,
    floconNetworkPlugin: FloconNetworkPlugin,
): MockNetworkResponse? {
    val url =  request.url.toString()
    val method = request.method
    return floconNetworkPlugin.mocks.firstOrNull {
        it.expectation.matches(
            url = url,
            method = method
        )
    }
}

internal fun executeMock(request: Request, mock: MockNetworkResponse): Response {
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