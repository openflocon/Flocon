package io.github.openflocon.flocon.okhttp

import io.github.openflocon.flocon.plugins.network.FloconNetworkPlugin
import io.github.openflocon.flocon.plugins.network.model.MockNetworkResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.Response.*
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

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

@Throws(IOException::class)
internal fun executeMock(request: Request, mock: MockNetworkResponse): Response {
    if (mock.response.delay > 0) {
        try {
            Thread.sleep(mock.response.delay)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    when(val response = mock.response) {
        is MockNetworkResponse.Response.Body -> {
            val body = response.body.toResponseBody(
                response.mediaType.toMediaTypeOrNull()
            )

            return Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .message(getHttpMessage(response.httpCode))
                .code(response.httpCode)
                .body(body)
                .apply {
                    response.headers.forEach { (name, value) ->
                        addHeader(name, value)
                    }
                }
                .build()
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