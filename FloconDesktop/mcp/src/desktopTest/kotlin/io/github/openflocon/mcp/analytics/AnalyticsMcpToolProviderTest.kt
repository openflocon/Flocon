package io.github.openflocon.mcp.analytics

import androidx.paging.PagingData
import io.github.openflocon.domain.analytics.models.AnalyticsEventCountDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsIdentifierDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsPropertyDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsSearchDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsTableId
import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
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

class AnalyticsMcpToolProviderTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample", lastAppInstance = 2))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
    )
    private val analyticsRepository = FakeAnalyticsRepository(
        events = listOf(
            event("old", "amplitude", "app_open", createdAt = 1, appInstance = 1),
            event("e1", "amplitude", "app_open", createdAt = 10),
            event("e2", "amplitude", "screen_view", createdAt = 20, "screen" to "feed"),
            event("e3", "firebase", "screen_view", createdAt = 30, "screen" to "profile"),
            event("e4", "amplitude", "post_liked", createdAt = 40, "postId" to "42"),
        ),
    )

    private val server = McpTestServer(
        toolProviders = listOf(
            AnalyticsMcpToolProvider(
                analyticsRepository = analyticsRepository,
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

        assertEquals(setOf("list_analytics_tables", "list_analytics_events", "count_analytics_events"), tools.map { it.name }.toSet())
        assertTrue(tools.all { it.annotations?.readOnlyHint == true })
    }

    @Test
    fun `list_analytics_tables`() = runBlocking {
        val result = server.callTool("list_analytics_tables").json()

        assertEquals(listOf("amplitude", "firebase"), result["analyticsTableIds"]!!.jsonArray.map { it.jsonPrimitive.content })
    }

    @Test
    fun `list_analytics_events returns the current session, newest first, with the properties`() = runBlocking {
        val result = server.callTool("list_analytics_events").json()

        assertEquals(listOf("e4", "e3", "e2", "e1"), result.eventIds())
        assertEquals(false, result["hasMore"]!!.jsonPrimitive.boolean)
        val liked = result["events"]!!.jsonArray.first().jsonObject
        assertEquals("post_liked", liked["eventName"]!!.jsonPrimitive.content)
        assertEquals("42", liked["properties"]!!.jsonObject["postId"]!!.jsonPrimitive.content)
    }

    @Test
    fun `list_analytics_events filters`() = runBlocking {
        assertEquals(
            listOf("e3", "e2"),
            server.callTool("list_analytics_events", mapOf("eventNames" to listOf("screen_view"))).json().eventIds(),
        )
        assertEquals(
            listOf("e3"),
            server.callTool("list_analytics_events", mapOf("analyticsTableId" to "firebase")).json().eventIds(),
        )
        assertEquals(
            listOf("e3"),
            server.callTool("list_analytics_events", mapOf("text" to "profile")).json().eventIds(),
        )
        assertEquals(
            listOf("e3", "e4"),
            server.callTool("list_analytics_events", mapOf("sinceTimestamp" to 30, "order" to "oldest_first")).json().eventIds(),
        )
        assertEquals(
            listOf("old", "e1"),
            server.callTool("list_analytics_events", mapOf("includeOldSessions" to true, "order" to "oldest_first", "limit" to 2)).json().eventIds(),
        )
    }

    @Test
    fun `list_analytics_events tells when there are more events`() = runBlocking {
        val result = server.callTool("list_analytics_events", mapOf("limit" to 3)).json()

        assertEquals(3, result["returned"]!!.jsonPrimitive.int)
        assertEquals(true, result["hasMore"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `invalid arguments are errors`() = runBlocking {
        val result = server.callTool("list_analytics_events", mapOf("order" to "random"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("random"))
    }

    @Test
    fun `count_analytics_events per event name`() = runBlocking {
        val result = server.callTool("count_analytics_events").json()

        assertEquals(4, result["total"]!!.jsonPrimitive.int)
        val counts = result["events"]!!.jsonArray.map { it.jsonObject["eventName"]!!.jsonPrimitive.content to it.jsonObject["count"]!!.jsonPrimitive.int }
        assertEquals(listOf("screen_view" to 2, "app_open" to 1, "post_liked" to 1), counts)
    }

    private fun JsonObject.eventIds(): List<String> = this["events"]!!.jsonArray.map { it.jsonObject["eventId"]!!.jsonPrimitive.content }

    private fun event(
        id: String,
        table: String,
        name: String,
        createdAt: Long,
        vararg properties: Pair<String, String>,
        appInstance: Long = 2,
    ) = AnalyticsItemDomainModel(
        itemId = id,
        analyticsTableId = table,
        createdAt = createdAt,
        createdAtFormatted = createdAt.toString(),
        eventName = name,
        properties = properties.map { AnalyticsPropertyDomainModel(name = it.first, value = it.second) },
        appInstance = appInstance,
    )
}

/**
 * Applies the filters like the Room queries
 */
private class FakeAnalyticsRepository(
    private val events: List<AnalyticsItemDomainModel>,
) : AnalyticsRepository {

    private fun filter(search: AnalyticsSearchDomainModel) = events.filter { event ->
        (search.analyticsTableId == null || event.analyticsTableId == search.analyticsTableId) &&
            (search.appInstance == null || event.appInstance == search.appInstance) &&
            (search.sinceTimestamp == null || event.createdAt >= search.sinceTimestamp!!) &&
            (search.eventNames == null || event.eventName in search.eventNames!!) &&
            (
                search.text == null ||
                    event.eventName.contains(search.text!!) ||
                    event.properties.any { it.name.contains(search.text!!) || it.value.contains(search.text!!) }
                )
    }

    override suspend fun searchAnalytics(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        search: AnalyticsSearchDomainModel,
        newestFirst: Boolean,
        limit: Int,
    ): List<AnalyticsItemDomainModel> = filter(search)
        .sortedBy { it.createdAt }
        .let { if (newestFirst) it.reversed() else it }
        .take(limit)

    override suspend fun countAnalyticsByEventName(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        search: AnalyticsSearchDomainModel,
    ): List<AnalyticsEventCountDomainModel> = filter(search)
        .groupingBy { it.eventName }
        .eachCount()
        .map { AnalyticsEventCountDomainModel(eventName = it.key, count = it.value) }
        .sortedWith(compareByDescending<AnalyticsEventCountDomainModel> { it.count }.thenBy { it.eventName })

    override fun observeDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<AnalyticsIdentifierDomainModel>> = flowOf(events.map { it.analyticsTableId }.distinct().map { AnalyticsIdentifierDomainModel(id = it, name = it) })

    override fun observeAnalytics(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsTableId: AnalyticsTableId,
        filter: String?,
    ): Flow<PagingData<AnalyticsItemDomainModel>> = error("not used")

    override suspend fun getAnalytics(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        analyticsTableId: AnalyticsTableId,
        filter: String?,
    ): List<AnalyticsItemDomainModel> = error("not used")

    override suspend fun deleteAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsId: AnalyticsIdentifierDomainModel) = error("not used")

    override fun observeAnalyticsById(id: String, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<AnalyticsItemDomainModel?> = error("not used")

    override suspend fun selectDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsTableId: AnalyticsTableId) = error("not used")

    override fun observeSelectedDeviceAnalytics(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<AnalyticsIdentifierDomainModel?> = error("not used")

    override suspend fun deleteAnalyticsItem(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsItemId: String) = error("not used")

    override suspend fun deleteBefore(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, analyticsItemId: String) = error("not used")

    override suspend fun deleteOldSessions(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = error("not used")
}
