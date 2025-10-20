package io.github.openflocon.flocon.ktor

import io.github.openflocon.flocon.plugins.network.model.BadQualityConfig
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

internal suspend fun executeBadQuality(
    badQualityConfig: BadQualityConfig,
    request: HttpRequestBuilder,
    client: HttpClient,
): HttpClientCall? {
    if (badQualityConfig.latency.shouldSimulateLatency()) {
        delay(badQualityConfig.latency.getRandomLatency())
    }

    return failResponseIfNeeded(
        badQualityConfig = badQualityConfig,
        request = request,
        client = client,
    )
}

@OptIn(InternalAPI::class)
internal fun failResponseIfNeeded(
    badQualityConfig: BadQualityConfig,
    request: HttpRequestBuilder,
    client: HttpClient,
): HttpClientCall? {
    if (badQualityConfig.shouldFail()) {
        badQualityConfig.selectRandomError()?.let { selectedError ->
            when (val t = selectedError.type) {
                is BadQualityConfig.Error.Type.Body -> {
                    val bodyBytes = t.errorBody.encodeToByteArray()

                    // maybe add headers ?
                    val headers = HeadersBuilder().apply {
                    }.build()

                    val responseData = HttpResponseData(
                        statusCode = HttpStatusCode.fromValue(t.errorCode),
                        requestTime = GMTDate(),
                        headers = headers,
                        version = HttpProtocolVersion.HTTP_1_1,
                        body = ByteReadChannel(bodyBytes),
                        callContext = client.coroutineContext,
                    )

                    return HttpClientCall(client, request.build(), responseData)
                }

                is BadQualityConfig.Error.Type.ErrorThrow -> {
                    val error = t.generate()
                    if (error != null) {
                        throw error
                    }
                }
            }
        }
    }
    return null
}