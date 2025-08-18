package io.github.openflocon.flocondesktop.features.network.model.badquality

import java.util.UUID

data class BadQualityConfigUiModel(
    val isEnabled: Boolean,
    val latency: LatencyConfig,
    val errorProbability: Double, // chance of triggering an error
    val errors: List<Error>, // list of errors
) {
    data class LatencyConfig(
        val triggerProbability: Float,
        val minLatencyMs: Long,
        val maxLatencyMs: Long,
    )
    data class Error(
        val uuid: String = UUID.randomUUID().toString(),
        val weight: Float, // increase the probability of being triggered vs all others errors
        val httpCode: Int,
        val body: String,
        val contentType: String, // "application/json"
    )
}
