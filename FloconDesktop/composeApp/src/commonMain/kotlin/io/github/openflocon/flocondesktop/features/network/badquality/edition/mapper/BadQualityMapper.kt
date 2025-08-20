package io.github.openflocon.flocondesktop.features.network.badquality.edition.mapper

import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.BadQualityConfigUiModel
import kotlin.time.Instant

fun BadQualityConfigDomainModel.toUi() = BadQualityConfigUiModel(
    id = id,
    name = name,
    createdAt = createdAt.toEpochMilliseconds(),
    isEnabled = isEnabled,
    latency = latency.toUi(),
    errorProbability = errorProbability,
    errors = errors.map { error ->
        error.toUi()
    },
)

private fun BadQualityConfigDomainModel.Error.toUi() = BadQualityConfigUiModel.Error(
    weight = weight,
    type = when(val t = type) {
        is BadQualityConfigDomainModel.Error.Type.Body -> BadQualityConfigUiModel.Error.Type.Body(
            httpCode = t.httpCode,
            body = t.body,
            contentType = t.contentType,
        )
        is BadQualityConfigDomainModel.Error.Type.Exception -> BadQualityConfigUiModel.Error.Type.Exception(
            classPath = t.classPath,
        )
    }
)

private fun BadQualityConfigDomainModel.LatencyConfig.toUi() = BadQualityConfigUiModel.LatencyConfig(
    triggerProbability = triggerProbability,
    minLatencyMs = minLatencyMs,
    maxLatencyMs = maxLatencyMs,
)

fun BadQualityConfigUiModel.toDomain() = BadQualityConfigDomainModel(
    id = id,
    name = name,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    isEnabled = isEnabled,
    latency = latency.toDomain(),
    errorProbability = errorProbability,
    errors = errors.map { error ->
        error.toDomain()
    },
)

private fun BadQualityConfigUiModel.Error.toDomain() = BadQualityConfigDomainModel.Error(
    weight = weight,
    type = when(val t = type) {
        is BadQualityConfigUiModel.Error.Type.Body -> BadQualityConfigDomainModel.Error.Type.Body(
            httpCode = t.httpCode,
            body = t.body,
            contentType = t.contentType,
        )
        is BadQualityConfigUiModel.Error.Type.Exception -> BadQualityConfigDomainModel.Error.Type.Exception(
            classPath = t.classPath,
        )
    }
)

private fun BadQualityConfigUiModel.LatencyConfig.toDomain() = BadQualityConfigDomainModel.LatencyConfig(
    triggerProbability = triggerProbability,
    minLatencyMs = minLatencyMs,
    maxLatencyMs = maxLatencyMs,
)
