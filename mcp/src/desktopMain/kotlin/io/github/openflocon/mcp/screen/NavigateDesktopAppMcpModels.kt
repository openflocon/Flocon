package io.github.openflocon.mcp.screen

import kotlinx.serialization.Serializable

@Serializable
internal data class NavigateDesktopAppMcpResult(
    // what was changed in Flocon Desktop, in the order it was applied
    val applied: List<String>,
    // to relay to the user: Flocon is not brought to the front
    val message: String,
)
