package io.github.openflocon.domain.network.models

import kotlin.time.Instant

typealias BadQualityConfigId = String

data class BadQualityConfigDomainModel(
    val id: BadQualityConfigId,
    val isEnabled: Boolean,
    val name: String,
    val createdAt: Instant,
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
        val weight: Float, // increase the probability of being triggered vs all others errors
        val httpCode: Int,
        val body: String,
        val contentType: String, // "application/json"
    )
}
