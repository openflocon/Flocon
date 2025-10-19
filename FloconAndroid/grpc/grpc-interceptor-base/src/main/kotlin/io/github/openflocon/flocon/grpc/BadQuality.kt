package io.github.openflocon.flocon.grpc

import io.github.openflocon.flocon.plugins.network.model.BadQualityConfig
import java.io.IOException

@Throws(IOException::class)
internal fun executeBadQuality(
    badQualityConfig: BadQualityConfig,
) {
    if (badQualityConfig.latency.shouldSimulateLatency()) {
        try {
            Thread.sleep(badQualityConfig.latency.getRandomLatency())
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    // only able to throw error for grpc
    failResponseIfNeeded(
        badQualityConfig = badQualityConfig,
    )
}

@Throws(IOException::class)
internal fun failResponseIfNeeded(
    badQualityConfig: BadQualityConfig,
) {
    if (badQualityConfig.shouldFail()) {
        badQualityConfig.selectRandomError()?.let { selectedError ->
            when (val t = selectedError.type) {
                is BadQualityConfig.Error.Type.Body -> {
                    // not possible for grpc
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
}