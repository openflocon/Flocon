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
    httpCode = httpCode,
    body = body,
    contentType = contentType,
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
    httpCode = httpCode,
    body = body,
    contentType = contentType,
)

private fun BadQualityConfigUiModel.LatencyConfig.toDomain() = BadQualityConfigDomainModel.LatencyConfig(
    triggerProbability = triggerProbability,
    minLatencyMs = minLatencyMs,
    maxLatencyMs = maxLatencyMs,
)
