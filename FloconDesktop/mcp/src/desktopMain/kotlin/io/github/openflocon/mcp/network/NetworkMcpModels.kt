package io.github.openflocon.mcp.network

import kotlinx.serialization.Serializable

@Serializable
internal data class NetworkTargetMcpModel(
    val deviceId: String,
    val packageName: String,
    val currentSessionId: Long,
)

@Serializable
internal data class NetworkCallsMcpResult(
    val target: NetworkTargetMcpModel,
    val totalMatching: Int,
    val returned: Int,
    val calls: List<NetworkCallSummaryMcpModel>,
)

@Serializable
internal data class NetworkCallSummaryMcpModel(
    val callId: String,
    val type: String,
    val method: String,
    val url: String,
    val startTime: String,
    val startTimeMs: Long,
    val state: String,
    val status: String?,
    val httpCode: Int?,
    val durationMs: Double?,
    val requestSize: Long,
    val responseSize: Long?,
    val graphQlOperation: String?,
    val isMocked: Boolean,
    val isReplayed: Boolean,
    val error: String?,
    val sessionId: Long,
    val isCurrentSession: Boolean,
)

@Serializable
internal data class NetworkCallDetailMcpModel(
    val deviceId: String,
    val packageName: String,
    val summary: NetworkCallSummaryMcpModel,
    val request: NetworkRequestMcpModel,
    val response: NetworkResponseMcpModel?,
    val curl: String?,
)

@Serializable
internal data class NetworkRequestMcpModel(
    val headers: Map<String, String>,
    val body: NetworkBodyMcpModel?,
    val graphQl: GraphQlRequestMcpModel?,
    val webSocketEvent: String?,
)

@Serializable
internal data class GraphQlRequestMcpModel(
    val operationType: String,
    val query: String,
)

@Serializable
internal data class NetworkResponseMcpModel(
    val contentType: String?,
    val headers: Map<String, String>?,
    val body: NetworkBodyMcpModel?,
    val isImage: Boolean,
    val grpcStatus: String?,
    val graphQlSuccess: Boolean?,
    val error: String?,
)

@Serializable
internal data class NetworkBodyMcpModel(
    val content: String,
    val truncated: Boolean,
    val totalLength: Int,
)
