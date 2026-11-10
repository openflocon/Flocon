package io.github.openflocon.mcp.devices

import kotlinx.serialization.Serializable

@Serializable
internal data class DevicesMcpResult(
    val devices: List<DeviceMcpModel>,
)

@Serializable
internal data class DeviceMcpModel(
    val deviceId: String,
    val name: String,
    val platform: String,
    // "default" (localhost) or "wifi" (address given to the connect module)
    val connectionType: String,
    val isSelectedInFlocon: Boolean,
    val apps: List<DeviceAppMcpModel>,
)

@Serializable
internal data class DeviceAppMcpModel(
    val packageName: String,
    val name: String,
    val floconVersion: String,
    val isSelectedInFlocon: Boolean,
    val isConnected: Boolean,
    val currentSessionId: Long,
)
