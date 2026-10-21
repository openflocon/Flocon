package io.github.openflocon.flocondesktop.features.network.badquality.list.mapper

import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.flocondesktop.features.network.badquality.list.model.NetworkBadQualityLineUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.roundToInt

fun BadQualityConfigDomainModel.toLineUi() = NetworkBadQualityLineUiModel(
    id = id,
    name = name,
    isEnabled = isEnabled,
    trigger = latency.triggerProbability.toPercent(),
    latency = "${latency.minLatencyMs} - ${latency.maxLatencyMs} ms",
    errorRate = errorProbability.toPercent(),
    errors = errors.map { error ->
        when (val type = error.type) {
            is BadQualityConfigDomainModel.Error.Type.Body -> NetworkBadQualityLineUiModel.ErrorUi(
                label = type.httpCode.toString(),
                isException = false,
            )

            is BadQualityConfigDomainModel.Error.Type.Exception -> NetworkBadQualityLineUiModel.ErrorUi(
                // the simple name of the class: the full path is too long for a line
                label = type.classPath.substringAfterLast('.'),
                isException = true,
            )
        }
    }.toImmutableList(),
)

// the probabilities are stored between 0 and 1
private fun Double.toPercent(): String = "${(this * 100).roundToInt()}%"
