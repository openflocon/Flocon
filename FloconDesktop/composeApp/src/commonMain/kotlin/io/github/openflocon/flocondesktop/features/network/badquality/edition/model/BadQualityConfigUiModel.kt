package io.github.openflocon.flocondesktop.features.network.badquality.edition.model

import java.util.UUID

data class BadQualityConfigUiModel(
    val id: String,
    val name: String,
    val createdAt: Long,
    val isEnabled: Boolean,
    val latency: LatencyConfig,
    val errorProbability: Double, // chance of triggering an error
    val errors: List<Error>, // list of errors
) {
    data class LatencyConfig(
        val triggerProbability: Double,
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

fun previewBadQualityConfigUiModel(errorCount: Int) = BadQualityConfigUiModel(
    id = "id",
    name = "config_name",
    isEnabled = true,
    latency = BadQualityConfigUiModel.LatencyConfig(
        triggerProbability = 0.1,
        minLatencyMs = 100,
        maxLatencyMs = 200,
    ),
    errorProbability = 0.8,
    createdAt = System.currentTimeMillis(),
    errors = List(errorCount) {
        BadQualityConfigUiModel.Error(
            weight = 1f,
            httpCode = 500,
            body = "{\"error\":\"...\"}",
            contentType = "application/json"
        )
    }
)
