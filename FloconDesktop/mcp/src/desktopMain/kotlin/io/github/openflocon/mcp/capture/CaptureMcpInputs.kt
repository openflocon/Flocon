package io.github.openflocon.mcp.capture

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.Serializable

@Serializable
internal data class TakeScreenshotInput(
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class StartScreenRecordingInput(
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class StopScreenRecordingInput(
    @McpDescription(
        "Device id (see list_devices) of the recording to stop. " +
            "Defaults to the only running recording, or to the device currently selected in Flocon.",
    )
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments
