package io.github.openflocon.flocondesktop.features.network.mapper

import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.flocondesktop.features.network.model.badquality.BadQualityConfigUiModel


fun toUi(model: BadQualityConfigDomainModel?) = model?.let {
    BadQualityConfigUiModel(
        isEnabled = it.isEnabled,
        latency = toUi(it.latency),
        errorProbability = it.errorProbability,
        errors = it.errors.map { error ->
            toUi(error)
        }
    )
}

private fun toUi(error: BadQualityConfigDomainModel.Error) = BadQualityConfigUiModel.Error(
    weight = error.weight,
    httpCode = error.httpCode,
    body = error.body,
    contentType = error.contentType,
)

private fun toUi(model: BadQualityConfigDomainModel.LatencyConfig) =
    BadQualityConfigUiModel.LatencyConfig(
        triggerProbability = model.triggerProbability,
        minLatencyMs = model.minLatencyMs,
        maxLatencyMs = model.maxLatencyMs,
    )


fun toDomain(model: BadQualityConfigUiModel) = BadQualityConfigDomainModel(
    isEnabled = model.isEnabled,
    latency = toDomain(model.latency),
    errorProbability = model.errorProbability,
    errors = model.errors.map { error ->
        toDomain(error)
    }
)

private fun toDomain(error: BadQualityConfigUiModel.Error) = BadQualityConfigDomainModel.Error(
    weight = error.weight,
    httpCode = error.httpCode,
    body = error.body,
    contentType = error.contentType,
)

private fun toDomain(model: BadQualityConfigUiModel.LatencyConfig) =
    BadQualityConfigDomainModel.LatencyConfig(
        triggerProbability = model.triggerProbability,
        minLatencyMs = model.minLatencyMs,
        maxLatencyMs = model.maxLatencyMs,
    )
