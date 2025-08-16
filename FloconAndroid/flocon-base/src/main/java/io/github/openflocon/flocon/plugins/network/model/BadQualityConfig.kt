package io.github.openflocon.flocon.plugins.network.model

data class BadQualityConfig(
    val latency: LatencyConfig,
    val errorProbability: Double, // chance of triggering an error
    val errors: List<Error>, // list of errors
) {
    class LatencyConfig(
        val latencyTriggerProbability: Float,
        val minLatencyMs: Long,
        val maxLatencyMs: Long,
    )
    class Error(
        val weight: Float, // increase the probability of being triggered vs all others errors
        val errorCode: Int,
        val errorBody: String,
        val errorContentType: String, // "application/json"
    )
}