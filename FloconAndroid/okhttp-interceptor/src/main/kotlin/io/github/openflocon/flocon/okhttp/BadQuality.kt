package io.github.openflocon.flocon.okhttp

import io.github.openflocon.flocon.plugins.network.model.BadQualityConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

@Throws(IOException::class)
internal fun executeBadQuality(
    badQualityConfig: BadQualityConfig,
    request: Request
): Response? {
    if (badQualityConfig.latency.shouldSimulateLatency()) {
        try {
            Thread.sleep(badQualityConfig.latency.getRandomLatency())
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    return failResponseIfNeeded(
        badQualityConfig = badQualityConfig,
        request = request,
    )
}

@Throws(IOException::class)
internal fun failResponseIfNeeded(
    badQualityConfig: BadQualityConfig,
    request: Request,
): Response? {
    if (badQualityConfig.shouldFail()) {
        badQualityConfig.selectRandomError()?.let { selectedError ->
            when (val t = selectedError.type) {
                is BadQualityConfig.Error.Type.Body -> {
                    val errorBody = t.errorBody.toResponseBody(t.errorContentType.toMediaTypeOrNull())

                    return Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(t.errorCode) // Utiliser le code d'erreur configuré
                        .message(getHttpMessage(t.errorCode))
                        .body(errorBody)
                        .build()
                }

                is BadQualityConfig.Error.Type.ErrorThrow -> {
                    val error = t.generate()
                    if (error is IOException) {
                        throw error //okhttp accepts only IOException
                    } else if (error is Throwable) {
                        throw IOException(error)
                    }
                }
            }
        }
    }
    return null
}