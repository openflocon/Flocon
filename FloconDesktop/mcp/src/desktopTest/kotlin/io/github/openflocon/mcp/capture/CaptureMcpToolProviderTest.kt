package io.github.openflocon.mcp.capture

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.RecordingDomainModel
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.targetResolver
import io.modelcontextprotocol.kotlin.sdk.types.ImageContent
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CaptureMcpToolProviderTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample"))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
    )

    private val tempDir = Files.createTempDirectory("flocon-capture-test").toFile()
    private val screenshotFile = File(tempDir, "screenshot.png").apply { writeBytes(byteArrayOf(1, 2, 3)) }

    private class FakeCapturer(private val screenshotFile: File) : ScreenCapturer {
        var screenshotResult: Either<Throwable, String> = Success(screenshotFile.absolutePath)
        var startResult: Either<Throwable, RecordingDomainModel>? = null
        val screenshotTargets = mutableListOf<DeviceIdAndPackageNameDomainModel>()
        val started = mutableListOf<DeviceIdAndPackageNameDomainModel>()
        val stopped = mutableListOf<RecordingDomainModel>()

        override suspend fun takeScreenshot(target: DeviceIdAndPackageNameDomainModel): Either<Throwable, String> {
            screenshotTargets += target
            return screenshotResult
        }

        override suspend fun startRecording(target: DeviceIdAndPackageNameDomainModel): Either<Throwable, RecordingDomainModel> {
            started += target
            return startResult ?: Success(RecordingDomainModel.IosSimulator(filePath = "/tmp/record_${target.deviceId}.mp4"))
        }

        override suspend fun stopRecording(recording: RecordingDomainModel): Either<Throwable, String> {
            stopped += recording
            return Success((recording as RecordingDomainModel.IosSimulator).filePath)
        }
    }

    private class FakeWindowCapturer : FloconWindowCapturer {
        var result: Either<Throwable, FloconWindowCapture> =
            Success(FloconWindowCapture(pngBytes = byteArrayOf(1, 2, 3), width = 800, height = 600))
        var captures = 0

        override suspend fun capture(): Either<Throwable, FloconWindowCapture> {
            captures++
            return result
        }
    }

    private val capturer = FakeCapturer(screenshotFile)
    private val windowCapturer = FakeWindowCapturer()

    private val server = McpTestServer(
        toolProviders = listOf(
            CaptureMcpToolProvider(
                screenCapturer = capturer,
                windowCapturer = windowCapturer,
                targetResolver = targetResolver(devicesRepository),
            ),
        ),
    )

    @BeforeTest
    fun setUp() = runBlocking { server.start() }

    @AfterTest
    fun tearDown() = runBlocking {
        server.stop()
        tempDir.deleteRecursively()
        Unit
    }

    @Test
    fun `the device capture tools act on the device so none is read only`() = runBlocking {
        val tools = server.listTools()

        assertEquals(
            setOf("take_screenshot", "take_flocon_screenshot", "start_screen_recording", "stop_screen_recording"),
            tools.map { it.name }.toSet(),
        )
        val deviceTools = tools.filter { it.name != "take_flocon_screenshot" }
        assertTrue(deviceTools.none { it.annotations?.readOnlyHint == true })
        assertTrue(tools.all { it.annotations?.destructiveHint == false })
    }

    @Test
    fun `take_flocon_screenshot only reads Flocon`() = runBlocking {
        val tool = server.listTools().single { it.name == "take_flocon_screenshot" }

        assertEquals(true, tool.annotations?.readOnlyHint)
    }

    @Test
    fun `take_flocon_screenshot returns the size and the image`() = runBlocking {
        val result = server.callTool("take_flocon_screenshot")

        assertEquals(false, result.isError)
        val text = assertIs<TextContent>(result.content[0])
        val json = Json.parseToJsonElement(text.text).jsonObject
        assertEquals("800", json["width"]!!.jsonPrimitive.content)
        assertEquals("600", json["height"]!!.jsonPrimitive.content)
        val image = assertIs<ImageContent>(result.content[1])
        assertEquals("image/png", image.mimeType)
        assertEquals("AQID", image.data)
        assertEquals(1, windowCapturer.captures)
        assertTrue(capturer.screenshotTargets.isEmpty())
    }

    @Test
    fun `take_flocon_screenshot returns an error when the capture fails`() = runBlocking {
        windowCapturer.result = Failure(IllegalStateException("window not displayed"))

        val result = server.callTool("take_flocon_screenshot")

        assertEquals(true, result.isError)
    }

    @Test
    fun `take_flocon_screenshot returns an error when the image is too big to be returned`() = runBlocking {
        windowCapturer.result = Success(
            FloconWindowCapture(pngBytes = ByteArray(6 * 1024 * 1024), width = 4000, height = 3000),
        )

        val result = server.callTool("take_flocon_screenshot")

        assertEquals(true, result.isError)
    }

    @Test
    fun `take_screenshot returns the path and the image`() = runBlocking {
        val result = server.callTool("take_screenshot")

        assertEquals(false, result.isError)
        val text = assertIs<TextContent>(result.content[0])
        val json = Json.parseToJsonElement(text.text).jsonObject
        assertEquals("pixel", json["deviceId"]!!.jsonPrimitive.content)
        assertEquals("io.flocon.sample", json["packageName"]!!.jsonPrimitive.content)
        assertEquals(screenshotFile.absolutePath, json["filePath"]!!.jsonPrimitive.content)
        val image = assertIs<ImageContent>(result.content[1])
        assertEquals("image/png", image.mimeType)
        assertEquals("AQID", image.data)
        assertEquals("pixel", capturer.screenshotTargets.single().deviceId)
    }

    @Test
    fun `take_screenshot still returns the path without the image when the file is missing`() = runBlocking {
        capturer.screenshotResult = Success(File(tempDir, "missing.png").absolutePath)

        val result = server.callTool("take_screenshot")

        assertEquals(false, result.isError)
        assertEquals(1, result.content.size)
    }

    @Test
    fun `take_screenshot returns an error when the capture fails`() = runBlocking {
        capturer.screenshotResult = Failure(IllegalStateException("adb not found"))

        val result = server.callTool("take_screenshot")

        assertEquals(true, result.isError)
    }

    @Test
    fun `start then stop returns the file and stops the started recording`() = runBlocking {
        val started = server.callTool("start_screen_recording").json()
        assertEquals("pixel", started["deviceId"]!!.jsonPrimitive.content)
        assertEquals("true", started["recording"]!!.jsonPrimitive.content)

        val stopped = server.callTool("stop_screen_recording").json()

        assertEquals("/tmp/record_pixel.mp4", stopped["filePath"]!!.jsonPrimitive.content)
        assertEquals("pixel", stopped["deviceId"]!!.jsonPrimitive.content)
        assertEquals(RecordingDomainModel.IosSimulator(filePath = "/tmp/record_pixel.mp4"), capturer.stopped.single())
    }

    @Test
    fun `stop_screen_recording accepts an explicit device`() = runBlocking {
        server.callTool("start_screen_recording")

        val stopped = server.callTool("stop_screen_recording", mapOf("deviceId" to "pixel"))

        assertEquals(false, stopped.isError)
        assertEquals(1, capturer.stopped.size)
    }

    @Test
    fun `starting twice on the same device returns an error`() = runBlocking {
        server.callTool("start_screen_recording")

        val second = server.callTool("start_screen_recording")

        assertEquals(true, second.isError)
        assertEquals(1, capturer.started.size)
    }

    @Test
    fun `a recording can be started again once stopped`() = runBlocking {
        server.callTool("start_screen_recording")
        server.callTool("stop_screen_recording")

        val again = server.callTool("start_screen_recording")

        assertEquals(false, again.isError)
    }

    @Test
    fun `stop without recording returns an error`() = runBlocking {
        val result = server.callTool("stop_screen_recording")

        assertEquals(true, result.isError)
        assertTrue(capturer.stopped.isEmpty())
    }

    @Test
    fun `start returns an error when the recording cannot start`() = runBlocking {
        capturer.startResult = Failure(IllegalStateException("no simulator"))

        val result = server.callTool("start_screen_recording")

        assertEquals(true, result.isError)
        assertEquals(true, server.callTool("stop_screen_recording").isError)
    }
}
