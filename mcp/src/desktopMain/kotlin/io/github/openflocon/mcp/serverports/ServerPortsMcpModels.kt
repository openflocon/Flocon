package io.github.openflocon.mcp.serverports

import kotlinx.serialization.Serializable

@Serializable
internal data class ServerPortsMcpModel(
    val websocketPort: Int,
    val httpPort: Int,
)

@Serializable
internal data class ServerPortsMcpResult(
    // the ports the servers use now
    val effective: ServerPortsMcpModel,
    // the ports saved in the Flocon settings (they differ from the effective ones when a launch option forces a port)
    val saved: ServerPortsMcpModel,
    val defaults: ServerPortsMcpModel,
    val websocketPortForcedByLaunchOption: Boolean,
    val httpPortForcedByLaunchOption: Boolean,
    val mcpPort: Int,
)

@Serializable
internal data class SetServerPortsMcpResult(
    val effective: ServerPortsMcpModel,
    val message: String,
)
