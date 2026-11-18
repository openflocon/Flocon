package io.github.openflocon.mcp.serverports

import io.github.openflocon.mcp.McpDescription
import kotlinx.serialization.Serializable

@Serializable
internal data class SetServerPortsInput(
    @McpDescription("New port of the websocket server (1024..65535). Omit to keep the current one. 9023 puts the default back.")
    val websocketPort: Int? = null,
    @McpDescription("New port of the HTTP server (1024..65535). Omit to keep the current one. 9024 puts the default back.")
    val httpPort: Int? = null,
)
