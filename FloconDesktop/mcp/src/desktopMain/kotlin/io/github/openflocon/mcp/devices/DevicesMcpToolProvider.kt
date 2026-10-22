package io.github.openflocon.mcp.devices

import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.jsonResult
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.coroutines.flow.first

internal class DevicesMcpToolProvider(
    private val devicesRepository: DevicesRepository,
) : McpToolProvider {

    override fun register(server: McpToolServer) {
        server.addLoggedTool(
            name = "list_devices",
            title = "List devices",
            description = "Lists the devices and apps known by Flocon, with the one currently selected in the Flocon desktop app " +
                "(used by default by the other tools) and whether each app is currently connected.",
            inputSchema = ToolSchema(),
            annotations = readOnlyToolAnnotations,
        ) {
            jsonResult(listDevices())
        }
    }

    private suspend fun listDevices(): DevicesMcpResult {
        val currentDeviceId = devicesRepository.getCurrentDeviceId()
        val activeDevices = devicesRepository.activeDevices.first()
        return DevicesMcpResult(
            devices = devicesRepository.devices.first().map { device ->
                val selectedApp = devicesRepository.getDeviceSelectedApp(device.deviceId)
                DeviceMcpModel(
                    deviceId = device.deviceId,
                    name = device.deviceName,
                    platform = device.platform,
                    isSelectedInFlocon = device.deviceId == currentDeviceId,
                    apps = devicesRepository.observeDeviceApps(device.deviceId).first().map { app ->
                        DeviceAppMcpModel(
                            packageName = app.packageName,
                            name = app.name,
                            floconVersion = app.floconVersionOnDevice,
                            isSelectedInFlocon = device.deviceId == currentDeviceId && app.packageName == selectedApp?.packageName,
                            isConnected = activeDevices.any { it.deviceId == device.deviceId && it.packageName == app.packageName },
                            currentSessionId = app.lastAppInstance,
                        )
                    },
                )
            },
        )
    }
}
