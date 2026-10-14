package io.github.openflocon.mcp.badquality

import kotlinx.serialization.Serializable

@Serializable
internal data class BadNetworkConfigsMcpResult(
    val deviceId: String,
    val packageName: String,
    val configs: List<BadNetworkConfigMcpModel>,
)

@Serializable
internal data class BadNetworkConfigMcpModel(
    val configId: String,
    val name: String,
    val isEnabled: Boolean,
    val latency: BadNetworkLatencyMcpModel,
    val errorProbability: Double,
    val errors: List<BadNetworkErrorMcpModel>,
)

@Serializable
internal data class BadNetworkLatencyMcpModel(
    val triggerProbability: Double,
    val minLatencyMs: Long,
    val maxLatencyMs: Long,
)

@Serializable
internal data class BadNetworkErrorMcpModel(
    val type: String,
    val weight: Float,
    val httpCode: Int? = null,
    val contentType: String? = null,
    val body: String? = null,
    val exceptionClassPath: String? = null,
)

@Serializable
internal data class BadNetworkChangeMcpResult(
    val message: String,
    val config: BadNetworkConfigMcpModel?,
)
