package io.github.openflocon.mcp.capture

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.RecordingDomainModel
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.addTypedToolWithResult
import io.github.openflocon.mcp.hasExplicitTarget
import io.github.openflocon.mcp.mcpOutputJson
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.github.openflocon.mcp.writeToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.ImageContent
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Base64

internal class CaptureMcpToolProvider(
    private val screenCapturer: ScreenCapturer,
    private val windowCapturer: FloconWindowCapturer,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    private class RunningRecording(
        val target: DeviceIdAndPackageNameDomainModel,
        val recording: RecordingDomainModel,
    )

    private val recordingsLock = Mutex()

    /** Running recordings, by device id */
    private val recordings = mutableMapOf<String, RunningRecording>()

    override fun register(server: McpToolServer) {
        server.addTypedToolWithResult(
            name = "take_screenshot",
            title = "Take screenshot",
            description = "Takes a screenshot of the device screen (adb on Android, the booted Simulator on iOS) " +
                "and saves it as a PNG in the Flocon documents folder (Screenshot/). " +
                "The image is returned with the path so you can see the current screen.",
            annotations = writeToolAnnotations(destructive = false, idempotent = false),
            handler = ::takeScreenshot,
        )

        server.addLoggedTool(
            name = "take_flocon_screenshot",
            title = "Take Flocon screenshot",
            description = "Returns an image of what the Flocon desktop app currently displays (the screen selected in Flocon: " +
                "network calls, database, analytics...), not the screen of the device: use take_screenshot for the device. " +
                "Only the visible content of the window is captured, and the image may be scaled down. " +
                "Nothing is saved on the computer.",
            inputSchema = ToolSchema(),
            annotations = readOnlyToolAnnotations,
        ) {
            takeFloconScreenshot()
        }

        server.addTypedTool(
            name = "start_screen_recording",
            title = "Start screen recording",
            description = "Starts recording the device screen (adb on Android, the booted Simulator on iOS). " +
                "Reproduce the scenario, then call stop_screen_recording to get the video. " +
                "On Android, screenrecord stops by itself after 3 minutes. " +
                "Only one recording can run per device.",
            annotations = writeToolAnnotations(destructive = false, idempotent = false),
            handler = ::startRecording,
        )

        server.addTypedTool(
            name = "stop_screen_recording",
            title = "Stop screen recording",
            description = "Stops the recording started by start_screen_recording and saves the video as an mp4 " +
                "in the Flocon documents folder (ScreenRecord/). Returns the path of the video.",
            annotations = writeToolAnnotations(destructive = false, idempotent = false),
            handler = ::stopRecording,
        )
    }

    private suspend fun takeScreenshot(input: TakeScreenshotInput): CallToolResult {
        val target = targetResolver.resolve(input)
        val path = screenCapturer.takeScreenshot(target).fold(
            doOnFailure = { throw McpToolException("Unable to take the screenshot: ${it.message ?: it::class.simpleName}") },
            doOnSuccess = { it },
        )

        val bytes = withContext(Dispatchers.IO) {
            File(path).takeIf { it.isFile && it.length() <= MAX_IMAGE_BYTES }?.readBytes()
        }
        val result = ScreenshotMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            filePath = path,
            imageIncluded = bytes != null,
        )
        return CallToolResult(
            content = listOfNotNull(
                TextContent(text = mcpOutputJson.encodeToString(result)),
                bytes?.let { ImageContent(data = Base64.getEncoder().encodeToString(it), mimeType = "image/png") },
            ),
            isError = false,
        )
    }

    private suspend fun takeFloconScreenshot(): CallToolResult {
        val capture = windowCapturer.capture().fold(
            doOnFailure = { throw McpToolException("Unable to capture the Flocon window: ${it.message ?: it::class.simpleName}") },
            doOnSuccess = { it },
        )
        if (capture.pngBytes.size > MAX_IMAGE_BYTES) {
            throw McpToolException("The capture of the Flocon window is too big to be returned (${capture.pngBytes.size} bytes).")
        }
        return CallToolResult(
            content = listOf(
                TextContent(text = mcpOutputJson.encodeToString(FloconScreenshotMcpResult(width = capture.width, height = capture.height))),
                ImageContent(data = Base64.getEncoder().encodeToString(capture.pngBytes), mimeType = "image/png"),
            ),
            isError = false,
        )
    }

    private suspend fun startRecording(input: StartScreenRecordingInput): StartScreenRecordingMcpResult {
        val target = targetResolver.resolve(input)
        // the lock is held while starting, so two calls cannot start two recordings on the same device
        return recordingsLock.withLock {
            if (recordings.containsKey(target.deviceId)) {
                throw McpToolException(
                    "A screen recording is already running on device '${target.deviceId}': call stop_screen_recording first.",
                )
            }
            val recording = screenCapturer.startRecording(target).fold(
                doOnFailure = { throw McpToolException("Unable to start the recording: ${it.message ?: it::class.simpleName}") },
                doOnSuccess = { it },
            )
            recordings[target.deviceId] = RunningRecording(target = target, recording = recording)
            StartScreenRecordingMcpResult(deviceId = target.deviceId, packageName = target.packageName, recording = true)
        }
    }

    private suspend fun stopRecording(input: StopScreenRecordingInput): StopScreenRecordingMcpResult = recordingsLock.withLock {
        val running = findRecording(input)
        val path = screenCapturer.stopRecording(running.recording).fold(
            doOnFailure = { throw McpToolException("Unable to stop the recording: ${it.message ?: it::class.simpleName}") },
            doOnSuccess = { it },
        )
        recordings.remove(running.target.deviceId)
        StopScreenRecordingMcpResult(
            deviceId = running.target.deviceId,
            packageName = running.target.packageName,
            filePath = path,
        )
    }

    /** Must be called with [recordingsLock] */
    private suspend fun findRecording(input: StopScreenRecordingInput): RunningRecording {
        if (recordings.isEmpty()) throw McpToolException("No screen recording is running: call start_screen_recording first.")
        if (!input.hasExplicitTarget() && recordings.size == 1) return recordings.values.single()

        val deviceId = targetResolver.resolve(input).deviceId
        return recordings[deviceId] ?: throw McpToolException(
            "No screen recording is running on device '$deviceId'. Running on: ${recordings.keys.joinToString()}",
        )
    }

    private companion object {
        const val MAX_IMAGE_BYTES = 5L * 1024 * 1024
    }
}
