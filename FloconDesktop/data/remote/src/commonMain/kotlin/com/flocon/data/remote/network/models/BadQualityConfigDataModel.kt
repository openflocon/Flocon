package com.flocon.data.remote.network.models

import kotlinx.serialization.Serializable

@Serializable
data class BadQualityConfigDataModel(
    val latency: LatencyConfig,
    val errorProbability: Double, // chance of triggering an error
    val errors: List<Error>, // list of errors
) {
    @Serializable
    data class LatencyConfig(
        val latencyTriggerProbability: Double,
        val minLatencyMs: Long,
        val maxLatencyMs: Long,
    )

    @Serializable
    data class Error(
        val weight: Float, // increase the probability of being triggered vs all others errors
        val errorCode: Int,
        val errorBody: String,
        val errorContentType: String, // "application/json"
    )
}
