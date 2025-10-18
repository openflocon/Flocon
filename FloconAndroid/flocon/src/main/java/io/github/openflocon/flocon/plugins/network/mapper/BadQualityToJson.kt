package io.github.openflocon.flocon.plugins.network.mapper

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.plugins.network.model.BadQualityConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal fun BadQualityConfig.toJsonString(): String {
    return FloconEncoder.json.encodeToString<BadQualityConfigSerializable>(
        toSerializable()
    )
}

internal fun parseBadQualityConfig(jsonString: String): BadQualityConfig? {
    return try {
        val parsed = FloconEncoder.json.decodeFromString<BadQualityConfigSerializable>(
            jsonString
        )
        parsed.toDomain()
    } catch (t: Throwable) {
        FloconLogger.logError(t.message ?: "bad connection network parsing issue", t)
        null
    }
}
@Serializable
internal class BadQualityConfigSerializable(
    val latency: LatencySerializable,
    val errorProbability: Double,
    val errors: List<ErrorSerializable>,
) {
    @Serializable
    class LatencySerializable(
        val latencyTriggerProbability: Float,
        val minLatencyMs: Long,
        val maxLatencyMs: Long,
    )

    @Serializable
    class ErrorSerializable(
        val weight: Float,
        val errorCode: Int? = null,
        val errorBody: String? = null,
        val errorContentType: String? = null,
        val errorException: String? = null,
    )
}

internal fun BadQualityConfig.toSerializable(): BadQualityConfigSerializable {
    return BadQualityConfigSerializable(
        latency = BadQualityConfigSerializable.LatencySerializable(
            latencyTriggerProbability = latency.latencyTriggerProbability,
            minLatencyMs = latency.minLatencyMs,
            maxLatencyMs = latency.maxLatencyMs
        ),
        errorProbability = errorProbability,
        errors = errors.map { error ->
            when (val t = error.type) {
                is BadQualityConfig.Error.Type.Body -> BadQualityConfigSerializable.ErrorSerializable(
                    weight = error.weight,
                    errorCode = t.errorCode,
                    errorBody = t.errorBody,
                    errorContentType = t.errorContentType
                )
                is BadQualityConfig.Error.Type.ErrorThrow -> BadQualityConfigSerializable.ErrorSerializable(
                    weight = error.weight,
                    errorException = t.classPath
                )
            }
        }
    )
}

internal fun BadQualityConfigSerializable.toDomain(): BadQualityConfig {
    val latencyConfig = BadQualityConfig.LatencyConfig(
        latencyTriggerProbability = latency.latencyTriggerProbability,
        minLatencyMs = latency.minLatencyMs,
        maxLatencyMs = latency.maxLatencyMs
    )

    val errorsList = errors.map { e ->
        val type = if (!e.errorException.isNullOrEmpty()) {
            BadQualityConfig.Error.Type.ErrorThrow(e.errorException)
        } else {
            BadQualityConfig.Error.Type.Body(
                errorCode = e.errorCode ?: 0,
                errorBody = e.errorBody.orEmpty(),
                errorContentType = e.errorContentType.orEmpty()
            )
        }
        BadQualityConfig.Error(
            weight = e.weight,
            type = type
        )
    }

    return BadQualityConfig(
        latency = latencyConfig,
        errorProbability = errorProbability,
        errors = errorsList
    )
}