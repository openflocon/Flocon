package io.github.openflocon.mcp

import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.network.usecase.GenerateCurlCommandUseCase
import io.github.openflocon.mcp.devices.DevicesMcpToolProvider
import io.github.openflocon.mcp.network.NetworkMcpToolProvider
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SafeToolCallTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")

    /**
     * Like the error seen from a real integration: the lookup of the selected app fails
     * with a JVM error whose message is only the name of a Kotlin lambda class
     */
    private val brokenDevicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample", lastAppInstance = 1))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
        selectedAppError = NoClassDefFoundError(BROKEN_CLASS),
    )

    private val server = McpTestServer(
        listOf(
            DevicesMcpToolProvider(brokenDevicesRepository),
            NetworkMcpToolProvider(
                networkRepository = FakeNetworkRepository(calls = emptyMap()),
                targetResolver = targetResolver(brokenDevicesRepository),
                generateCurlCommandUseCase = GenerateCurlCommandUseCase(),
            ),
            object : McpToolProvider {
                override fun register(server: McpToolServer) {
                    server.addLoggedTool("failing_tool", "Failing", "Throws", ToolSchema(), readOnlyToolAnnotations) {
                        error("boom\nsecond line")
                    }
                    server.addLoggedTool("leaked_timeout_tool", "Timeout", "Leaks a timeout", ToolSchema(), readOnlyToolAnnotations) {
                        withTimeout(1) { kotlinx.coroutines.delay(1_000) }
                        error("not reached")
                    }
                    server.addLoggedTool("user_error_tool", "User error", "Throws a McpToolException", ToolSchema(), readOnlyToolAnnotations) {
                        throw McpToolException("No device / app is currently selected in Flocon.")
                    }
                }
            },
        ),
    )

    @BeforeTest
    fun setUp() = runBlocking { server.start() }

    @AfterTest
    fun tearDown() = runBlocking { server.stop() }

    @Test
    fun `a JVM error is returned as a tool error naming the tool, not as a bare class name`() = runBlocking {
        val result = server.callTool("list_devices")

        assertEquals(true, result.isError)
        val text = result.text()
        assertTrue(text.startsWith("Internal Flocon Desktop error in list_devices: "), text)
        assertTrue("Restart Flocon Desktop" in text, text)
    }

    @Test
    fun `a JVM error while resolving the device is a tool error too`() = runBlocking {
        val result = server.callTool("list_network_calls", mapOf(ARG_DEVICE_ID to "pixel"))

        assertEquals(true, result.isError)
        assertTrue(result.text().startsWith("Internal Flocon Desktop error in list_network_calls: "), result.text())
    }

    @Test
    fun `an explicit device and app never read the selected app`() = runBlocking {
        val result = server.callTool("list_network_calls", mapOf(ARG_DEVICE_ID to "pixel", ARG_PACKAGE_NAME to "io.flocon.sample"))

        assertFalse(result.isError == true, result.text())
    }

    @Test
    fun `an unexpected exception gives its type and the first line of its message`() = runBlocking {
        val result = server.callTool("failing_tool")

        assertEquals(true, result.isError)
        assertEquals("Internal Flocon Desktop error in failing_tool: IllegalStateException: boom.", result.text())
    }

    @Test
    fun `a timeout leaking from a tool is an internal error, not a dropped request`() = runBlocking {
        val result = server.callTool("leaked_timeout_tool")

        assertEquals(true, result.isError)
        assertTrue(result.text().startsWith("Internal Flocon Desktop error in leaked_timeout_tool: TimeoutCancellationException"), result.text())
    }

    @Test
    fun `McpToolException messages are kept as is`() = runBlocking {
        val result = server.callTool("user_error_tool")

        assertEquals(true, result.isError)
        assertEquals("No device / app is currently selected in Flocon.", result.text())
    }

    private companion object {
        const val BROKEN_CLASS = "io/github/openflocon/flocondesktop/core/data/device/DevicesRepositoryImpl\$getDeviceSelectedApp\$2"
    }
}
