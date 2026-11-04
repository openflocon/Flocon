package io.github.openflocon.mcp.timeline

import kotlinx.serialization.Serializable

@Serializable
internal data class TimelineWindowMcpModel(
    val fromMs: Long,
    val toMs: Long,
    val fromLocal: String,
    val toLocal: String,
    val timeZone: String,
    // why this window was chosen: always read it, the tool picks one when no time is given
    val reason: String,
)

@Serializable
internal data class GetTimelineMcpResult(
    val deviceId: String,
    val packageName: String,
    val window: TimelineWindowMcpModel,
    // the events of the window, before they are folded
    val eventCount: Int,
    val anomalyCount: Int,
    val anomalies: List<String>,
    val lines: List<String>,
    // null when no search was asked
    val found: Boolean?,
    val searchMatches: List<String>,
    val truncated: Boolean,
    val omittedCount: Int,
    val notes: List<String>,
)
