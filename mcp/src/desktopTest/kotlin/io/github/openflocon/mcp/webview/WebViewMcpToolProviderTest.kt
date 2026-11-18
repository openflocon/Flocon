package io.github.openflocon.mcp.webview

import androidx.paging.PagingData
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.webview.models.WebViewEventDomainModel
import io.github.openflocon.domain.webview.models.WebViewEventKind
import io.github.openflocon.domain.webview.repository.WebViewRepository
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.targetResolver
import kotlinx.coroutines.flow.Flow
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

class WebViewMcpToolProviderTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample"))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
    )
    private val webViewRepository = FakeWebViewRepository(
        events = listOf(
            event("e1", timestamp = 10, kind = WebViewEventKind.Console, label = "log", message = "hello"),
            event("e2", timestamp = 20, kind = WebViewEventKind.Console, label = "error", message = "x is undefined"),
            event("e3", timestamp = 30, kind = WebViewEventKind.Page, label = "page_finished", message = null),
            event("e4", timestamp = 40, kind = WebViewEventKind.Bridge, label = "Android.share", message = "hi"),
        ),
    )

    private val server = McpTestServer(
        toolProviders = listOf(
            WebViewMcpToolProvider(
                webViewRepository = webViewRepository,
                targetResolver = targetResolver(devicesRepository),
            ),
        ),
    )

    @BeforeTest
    fun setUp() = runBlocking { server.start() }

    @AfterTest
    fun tearDown() = runBlocking { server.stop() }

    @Test
    fun `the tool is read only`() = runBlocking {
        val tools = server.listTools()

        assertEquals(setOf("list_webview_events"), tools.map { it.name }.toSet())
        assertTrue(tools.all { it.annotations?.readOnlyHint == true })
    }

    @Test
    fun `list_webview_events returns the newest first`() = runBlocking {
        val result = server.callTool("list_webview_events").json()

        assertEquals(listOf("e4", "e3", "e2", "e1"), result.eventIds())
        assertEquals(false, result["hasMore"]!!.jsonPrimitive.boolean)
        val first = result["events"]!!.jsonArray.first().jsonObject
        assertEquals("bridge", first["kind"]!!.jsonPrimitive.content)
        assertEquals("Android.share", first["label"]!!.jsonPrimitive.content)
    }

    @Test
    fun `list_webview_events filters by kind, text and time`() = runBlocking {
        assertEquals(listOf("e2", "e1"), server.callTool("list_webview_events", mapOf("kind" to "console")).json().eventIds())
        assertEquals(listOf("e2"), server.callTool("list_webview_events", mapOf("text" to "undefined")).json().eventIds())
        assertEquals(
            listOf("e3", "e4"),
            server.callTool("list_webview_events", mapOf("sinceTimestamp" to 30, "order" to "oldest_first")).json().eventIds(),
        )
    }

    @Test
    fun `list_webview_events tells when there are more events`() = runBlocking {
        val result = server.callTool("list_webview_events", mapOf("limit" to 2)).json()

        assertEquals(2, result["returned"]!!.jsonPrimitive.int)
        assertEquals(true, result["hasMore"]!!.jsonPrimitive.boolean)
    }

    private fun JsonObject.eventIds(): List<String> = this["events"]!!.jsonArray.map { it.jsonObject["eventId"]!!.jsonPrimitive.content }

    private fun event(id: String, timestamp: Long, kind: WebViewEventKind, label: String, message: String?) = WebViewEventDomainModel(
        id = id,
        webViewId = "checkout",
        kind = kind,
        url = "https://example.com",
        label = label,
        message = message,
        sourceId = null,
        lineNumber = null,
        statusCode = null,
        result = null,
        timestamp = timestamp,
    )
}

private class FakeWebViewRepository(
    private val events: List<WebViewEventDomainModel>,
) : WebViewRepository {

    override suspend fun searchEvents(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        kind: WebViewEventKind?,
        sinceTimestamp: Long?,
        text: String?,
        newestFirst: Boolean,
        limit: Int,
    ): List<WebViewEventDomainModel> = events
        .filter { kind == null || it.kind == kind }
        .filter { sinceTimestamp == null || it.timestamp >= sinceTimestamp }
        .filter { text == null || it.label.contains(text) || it.message?.contains(text) == true }
        .sortedBy { it.timestamp }
        .let { if (newestFirst) it.reversed() else it }
        .take(limit)

    override fun observeEvents(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        kind: WebViewEventKind?,
        filter: String?,
    ): Flow<PagingData<WebViewEventDomainModel>> = error("not used")

    override suspend fun clearAll(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = error("not used")
}
