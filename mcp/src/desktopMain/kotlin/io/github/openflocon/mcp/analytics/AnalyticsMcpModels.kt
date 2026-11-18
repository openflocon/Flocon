package io.github.openflocon.mcp.analytics

import kotlinx.serialization.Serializable

@Serializable
internal data class AnalyticsTablesMcpResult(
    val deviceId: String,
    val packageName: String,
    val analyticsTableIds: List<String>,
)

@Serializable
internal data class AnalyticsEventsMcpResult(
    val deviceId: String,
    val packageName: String,
    val currentSessionId: Long,
    val returned: Int,
    val hasMore: Boolean,
    val events: List<AnalyticsEventMcpModel>,
)

@Serializable
internal data class AnalyticsEventMcpModel(
    val eventId: String,
    val analyticsTableId: String,
    val eventName: String,
    val timestamp: String,
    val timestampMs: Long,
    val properties: Map<String, String>,
    val sessionId: Long,
    val isCurrentSession: Boolean,
)

@Serializable
internal data class AnalyticsEventCountsMcpResult(
    val deviceId: String,
    val packageName: String,
    val total: Int,
    val events: List<AnalyticsEventCountMcpModel>,
)

@Serializable
internal data class AnalyticsEventCountMcpModel(
    val eventName: String,
    val count: Int,
)
