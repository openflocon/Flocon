package io.github.openflocon.mcp.accessibility

import io.github.openflocon.domain.accessibility.models.AccessibilityEventDomainModel
import io.github.openflocon.domain.accessibility.models.AccessibilityFocusedNodeDomainModel
import io.github.openflocon.domain.accessibility.models.AccessibilityStateDomainModel
import io.github.openflocon.domain.accessibility.repository.AccessibilityRepository
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.targetResolver
import io.github.openflocon.mcp.text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AccessibilityMcpToolProviderTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample"))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
    )
    private val accessibilityRepository = FakeAccessibilityRepository(
        events = listOf(
            event(id = "3", type = "ANNOUNCEMENT", text = "Added to cart", createdAt = 3_000L),
            event(id = "2", type = "VIEW_FOCUSED", text = null, contentDescription = "Open the menu", className = "android.widget.ImageButton", createdAt = 2_000L, extras = mapOf("checked" to "false")),
            event(id = "1", type = "VIEW_FOCUSED", text = "Add to cart", className = "android.widget.Button", screen = "CheckoutActivity", createdAt = 1_000L),
        ),
        state = AccessibilityStateDomainModel(supported = true, enabled = true, touchExploration = true, services = listOf("TalkBack")),
    )

    private val server = McpTestServer(
        toolProviders = listOf(
            AccessibilityMcpToolProvider(
                accessibilityRepository = accessibilityRepository,
                targetResolver = targetResolver(devicesRepository),
            ),
        ),
    )

    @BeforeTest
    fun setUp() = runBlocking { server.start() }

    @AfterTest
    fun tearDown() = runBlocking { server.stop() }

    @Test
    fun `only the tools that change something are not read only`() = runBlocking {
        val tools = server.listTools()

        assertEquals(
            setOf(
                "list_accessibility_events",
                "get_accessibility_state",
                "get_accessibility_focused_node",
                "move_accessibility_focus",
                "clear_accessibility_events",
            ),
            tools.map { it.name }.toSet(),
        )
        assertEquals(
            setOf("list_accessibility_events", "get_accessibility_state", "get_accessibility_focused_node"),
            tools.filter { it.annotations?.readOnlyHint == true }.map { it.name }.toSet(),
        )
    }

    @Test
    fun `list_accessibility_events returns the events newest first with the types`() = runBlocking {
        val result = server.callTool("list_accessibility_events").json()

        assertEquals("pixel", result["deviceId"]!!.jsonPrimitive.content)
        assertEquals(listOf("ANNOUNCEMENT", "VIEW_FOCUSED"), result["types"]!!.jsonArray.map { it.jsonPrimitive.content })
        assertEquals(3, result["returned"]!!.jsonPrimitive.int)
        assertEquals(false, result["hasMore"]!!.jsonPrimitive.boolean)
        val events = result["events"]!!.jsonArray.map { it.jsonObject }
        assertEquals(listOf("3", "2", "1"), events.map { it["eventId"]!!.jsonPrimitive.content })
        assertEquals("1970-01-01T00:00:03Z", events[0]["timestamp"]!!.jsonPrimitive.content)
        assertEquals("false", events[1]["extras"]!!.jsonObject["checked"]!!.jsonPrimitive.content)
        assertEquals("CheckoutActivity", events[2]["screen"]!!.jsonPrimitive.content)
    }

    @Test
    fun `list_accessibility_events filters by type and text`() = runBlocking {
        val byType = server.callTool("list_accessibility_events", mapOf("type" to "view_focused")).json()
        assertEquals(listOf("2", "1"), byType["events"]!!.jsonArray.map { it.jsonObject["eventId"]!!.jsonPrimitive.content })

        val byText = server.callTool("list_accessibility_events", mapOf("text" to "menu")).json()
        assertEquals(listOf("2"), byText["events"]!!.jsonArray.map { it.jsonObject["eventId"]!!.jsonPrimitive.content })

        val both = server.callTool("list_accessibility_events", mapOf("type" to "ANNOUNCEMENT", "text" to "menu")).json()
        assertEquals(0, both["returned"]!!.jsonPrimitive.int)
    }

    @Test
    fun `list_accessibility_events reports hasMore`() = runBlocking {
        val result = server.callTool("list_accessibility_events", mapOf("limit" to 2)).json()

        assertEquals(2, result["events"]!!.jsonArray.size)
        assertEquals(true, result["hasMore"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `list_accessibility_events rejects a wrong limit`() = runBlocking {
        val result = server.callTool("list_accessibility_events", mapOf("limit" to 0))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("limit"))
    }

    @Test
    fun `get_accessibility_state returns the state of the device`() = runBlocking {
        val result = server.callTool("get_accessibility_state").json()

        assertEquals(true, result["known"]!!.jsonPrimitive.boolean)
        assertEquals(true, result["supported"]!!.jsonPrimitive.boolean)
        assertEquals(true, result["enabled"]!!.jsonPrimitive.boolean)
        assertEquals(true, result["touchExploration"]!!.jsonPrimitive.boolean)
        assertEquals(listOf("TalkBack"), result["services"]!!.jsonArray.map { it.jsonPrimitive.content })
    }

    @Test
    fun `get_accessibility_state is unknown until the device sends it`() = runBlocking {
        accessibilityRepository.state = null

        val result = server.callTool("get_accessibility_state").json()

        assertFalse(result["known"]!!.jsonPrimitive.boolean)
        assertNull(result["enabled"])
    }

    @Test
    fun `get_accessibility_focused_node returns the element the screen reader is on`() = runBlocking {
        val result = server.callTool("get_accessibility_focused_node").json()

        assertEquals(true, result["focused"]!!.jsonPrimitive.boolean)
        assertEquals("Item 2", result["text"]!!.jsonPrimitive.content)
        assertEquals(2, result["position"]!!.jsonPrimitive.int)
        assertEquals(3, result["count"]!!.jsonPrimitive.int)
        assertNull(result["moved"])
    }

    @Test
    fun `get_accessibility_focused_node says when nothing is focused`() = runBlocking {
        accessibilityRepository.focusedNode.value = null

        val result = server.callTool("get_accessibility_focused_node").json()

        assertFalse(result["focused"]!!.jsonPrimitive.boolean)
        assertNull(result["text"])
    }

    @Test
    fun `move_accessibility_focus goes to the next and previous elements`() = runBlocking {
        val next = server.callTool("move_accessibility_focus", mapOf("direction" to "next")).json()
        assertEquals("Item 3", next["text"]!!.jsonPrimitive.content)
        assertEquals(3, next["position"]!!.jsonPrimitive.int)
        assertEquals(true, next["moved"]!!.jsonPrimitive.boolean)

        val previous = server.callTool("move_accessibility_focus", mapOf("direction" to "PREVIOUS")).json()
        assertEquals("Item 2", previous["text"]!!.jsonPrimitive.content)
        assertEquals(true, previous["moved"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `move_accessibility_focus rejects an unknown direction`() = runBlocking {
        val result = server.callTool("move_accessibility_focus", mapOf("direction" to "up"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("direction"))
    }

    @Test
    fun `clear_accessibility_events deletes the events`() = runBlocking {
        val result = server.callTool("clear_accessibility_events").json()

        assertEquals(3, result["deleted"]!!.jsonPrimitive.int)
        assertTrue(accessibilityRepository.events.isEmpty())
    }
}

private fun event(
    id: String,
    type: String,
    text: String?,
    createdAt: Long,
    contentDescription: String? = null,
    className: String? = null,
    screen: String? = null,
    extras: Map<String, String> = emptyMap(),
) = AccessibilityEventDomainModel(
    id = id,
    type = type,
    text = text,
    contentDescription = contentDescription,
    className = className,
    screen = screen,
    bounds = null,
    extras = extras,
    createdAt = createdAt,
)

private class FakeAccessibilityRepository(
    var events: List<AccessibilityEventDomainModel>,
    var state: AccessibilityStateDomainModel?,
) : AccessibilityRepository {

    /** the focus order of the fake screen: 3 elements, the device answers a request with the element it ends on */
    val focusedNode = MutableStateFlow<AccessibilityFocusedNodeDomainModel?>(focusOrderElement(index = 1))

    override fun observeEvents(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        type: String?,
        text: String?,
    ): Flow<List<AccessibilityEventDomainModel>> = error("not used")

    override fun observeEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        id: String,
    ): Flow<AccessibilityEventDomainModel?> = error("not used")

    override fun observeTypes(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<String>> = error("not used")

    override fun observeFocusedNode(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<AccessibilityFocusedNodeDomainModel?> = focusedNode

    override suspend fun requestFocusedNode(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        // the device answers with the element that is focused: nothing changes
    }

    override suspend fun focusNext(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = moveFocus(1)

    override suspend fun focusPrevious(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = moveFocus(-1)

    override suspend fun activate(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        // the device answers with the element that is focused: nothing changes
    }

    private fun moveFocus(step: Int) {
        val index = focusedNode.value?.index ?: return
        if (index + step in 0..2) focusedNode.value = focusOrderElement(index + step)
    }

    override fun observeState(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<AccessibilityStateDomainModel?> = error("not used")

    override suspend fun searchEvents(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        type: String?,
        text: String?,
        limit: Int,
    ): List<AccessibilityEventDomainModel> = events
        .filter { type == null || it.type.equals(type, ignoreCase = true) }
        .filter {
            text == null || listOf(it.text, it.contentDescription, it.className, it.screen)
                .any { value -> value?.contains(text, ignoreCase = true) == true }
        }
        .sortedByDescending { it.createdAt }
        .take(limit)

    override suspend fun getTypes(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<String> = events.map { it.type }.distinct().sorted()

    override suspend fun getState(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): AccessibilityStateDomainModel? = state

    override suspend fun clearEvents(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Int = events.size.also { events = emptyList() }
}

private fun focusOrderElement(index: Int) = AccessibilityFocusedNodeDomainModel(
    found = true,
    text = "Item ${index + 1}",
    contentDescription = null,
    className = "Button",
    screen = "ListActivity",
    bounds = "[0,${index * 100}][100,${index * 100 + 100}]",
    extras = emptyMap(),
    index = index,
    count = 3,
    hasNext = index < 2,
    hasPrevious = index > 0,
)
