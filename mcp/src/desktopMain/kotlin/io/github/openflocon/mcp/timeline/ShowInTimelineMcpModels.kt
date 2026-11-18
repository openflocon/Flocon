package io.github.openflocon.mcp.timeline

import kotlinx.serialization.Serializable

@Serializable
internal data class ShowInTimelineMcpResult(
    // what was changed in Flocon, one sentence each
    val applied: List<String>,
    // the zone shown, and why this one
    val window: TimelineWindowMcpModel,
    // the event selected (source:id), absent when only the zone is shown
    val eventId: String?,
    // why the filters of the user hide the event, empty when it is displayed. The filters are never changed
    val hiddenBy: List<String>,
    // what to tell the user
    val message: String,
)
