package io.github.openflocon.mcp.crashreporter

import androidx.paging.PagingData
import io.github.openflocon.domain.crashreporter.models.CrashReportDomainModel
import io.github.openflocon.domain.crashreporter.repository.CrashReporterRepository
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.targetResolver
import io.github.openflocon.mcp.text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CrashReporterMcpToolProviderTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample"))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
    )
    private val crashReporterRepository = FakeCrashReporterRepository(
        crashes = listOf(
            crash("c1", timestamp = 10, type = "java.lang.NullPointerException", message = "user is null"),
            crash("c2", timestamp = 20, type = "java.lang.IllegalStateException", message = "bad state"),
            crash("c3", timestamp = 30, type = "java.lang.OutOfMemoryError", message = "heap"),
        ),
    )

    private val server = McpTestServer(
        toolProviders = listOf(
            CrashReporterMcpToolProvider(
                crashReporterRepository = crashReporterRepository,
                targetResolver = targetResolver(devicesRepository),
            ),
        ),
    )

    @BeforeTest
    fun setUp() = runBlocking { server.start() }

    @AfterTest
    fun tearDown() = runBlocking { server.stop() }

    @Test
    fun `all the tools are read only`() = runBlocking {
        val tools = server.listTools()

        assertEquals(setOf("list_crash_reports", "get_crash_report"), tools.map { it.name }.toSet())
        assertTrue(tools.all { it.annotations?.readOnlyHint == true })
    }

    @Test
    fun `list_crash_reports returns the newest first, without the stack trace`() = runBlocking {
        val result = server.callTool("list_crash_reports").json()

        assertEquals(listOf("c3", "c2", "c1"), result.crashIds())
        assertEquals(false, result["hasMore"]!!.jsonPrimitive.boolean)
        val first = result["crashes"]!!.jsonArray.first().jsonObject
        assertEquals("java.lang.OutOfMemoryError", first["exceptionType"]!!.jsonPrimitive.content)
        assertEquals("at com.flocon.Main.run(Main.kt:12)", first["firstStackTraceLine"]!!.jsonPrimitive.content)
        assertTrue("stackTrace" !in first)
    }

    @Test
    fun `list_crash_reports filters`() = runBlocking {
        assertEquals(listOf("c1"), server.callTool("list_crash_reports", mapOf("text" to "NullPointer")).json().crashIds())
        assertEquals(
            listOf("c2", "c3"),
            server.callTool("list_crash_reports", mapOf("sinceTimestamp" to 20, "order" to "oldest_first")).json().crashIds(),
        )
    }

    @Test
    fun `list_crash_reports tells when there are more crashes`() = runBlocking {
        val result = server.callTool("list_crash_reports", mapOf("limit" to 2)).json()

        assertEquals(2, result["returned"]!!.jsonPrimitive.int)
        assertEquals(true, result["hasMore"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `get_crash_report returns the stack trace`() = runBlocking {
        val result = server.callTool("get_crash_report", mapOf("crashId" to "c2")).json()

        assertEquals("c2", result["crashId"]!!.jsonPrimitive.content)
        assertEquals("bad state", result["exceptionMessage"]!!.jsonPrimitive.content)
        assertTrue(result["stackTrace"]!!.jsonPrimitive.content.contains("Main.run"))
    }

    @Test
    fun `get_crash_report with an unknown id is an error`() = runBlocking {
        val result = server.callTool("get_crash_report", mapOf("crashId" to "nope"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("nope"))
    }

    private fun JsonObject.crashIds(): List<String> = this["crashes"]!!.jsonArray.map { it.jsonObject["crashId"]!!.jsonPrimitive.content }

    private fun crash(id: String, timestamp: Long, type: String, message: String) = CrashReportDomainModel(
        id = id,
        timestamp = timestamp,
        exceptionType = type,
        exceptionMessage = message,
        stackTrace = "$type: $message\n\tat com.flocon.Main.run(Main.kt:12)",
    )
}

private class FakeCrashReporterRepository(
    private val crashes: List<CrashReportDomainModel>,
) : CrashReporterRepository {

    override suspend fun searchCrashReports(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sinceTimestamp: Long?,
        text: String?,
        newestFirst: Boolean,
        limit: Int,
    ): List<CrashReportDomainModel> = crashes
        .filter { sinceTimestamp == null || it.timestamp >= sinceTimestamp }
        .filter { text == null || it.exceptionType.contains(text) || it.exceptionMessage.contains(text) || it.stackTrace.contains(text) }
        .sortedBy { it.timestamp }
        .let { if (newestFirst) it.reversed() else it }
        .take(limit)

    override fun observeCrashReportById(id: String, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<CrashReportDomainModel?> =
        flowOf(crashes.firstOrNull { it.id == id })

    override fun observeCrashReports(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<PagingData<CrashReportDomainModel>> =
        error("not used")

    override suspend fun deleteCrashReport(crashId: String) = error("not used")

    override suspend fun clearAll(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = error("not used")
}
