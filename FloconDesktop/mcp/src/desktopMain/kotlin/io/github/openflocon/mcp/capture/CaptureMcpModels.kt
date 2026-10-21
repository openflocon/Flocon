package io.github.openflocon.mcp.capture

import kotlinx.serialization.Serializable

@Serializable
internal data class ScreenshotMcpResult(
    val deviceId: String,
    val packageName: String,
    /** Absolute path of the PNG saved on this computer */
    val filePath: String,
    /** False when the image is too big to be returned with the result, open [filePath] instead */
    val imageIncluded: Boolean,
)

@Serializable
internal data class StartScreenRecordingMcpResult(
    val deviceId: String,
    val packageName: String,
    val recording: Boolean,
)

@Serializable
internal data class StopScreenRecordingMcpResult(
    val deviceId: String,
    val packageName: String,
    /** Absolute path of the mp4 saved on this computer */
    val filePath: String,
)
