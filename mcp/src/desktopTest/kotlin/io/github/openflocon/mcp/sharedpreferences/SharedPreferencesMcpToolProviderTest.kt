package io.github.openflocon.mcp.sharedpreferences

import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceId
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel.Value
import io.github.openflocon.domain.sharedpreference.repository.SharedPreferencesRepository
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.targetResolver
import io.github.openflocon.mcp.text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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

class SharedPreferencesMcpToolProviderTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample", lastAppInstance = 2))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
    )
    private val repository = FakeSharedPreferencesRepository(
        files = listOf(DeviceSharedPreferenceDomainModel(id = "settings", name = "settings", type = "DataStore")),
        values = mapOf(
            "settings" to listOf(
                SharedPreferenceRowDomainModel("theme", Value.StringValue("dark")),
                SharedPreferenceRowDomainModel("launch_count", Value.IntValue(12)),
                SharedPreferenceRowDomainModel("onboarded", Value.BooleanValue(true)),
                SharedPreferenceRowDomainModel("tags", Value.StringSetValue(setOf("b", "a"))),
            ),
        ),
    )

    private val server = McpTestServer(
        toolProviders = listOf(
            SharedPreferencesMcpToolProvider(
                sharedPreferencesRepository = repository,
                targetResolver = targetResolver(devicesRepository),
            ).apply { answerTimeoutMs = 200 },
        ),
    )

    @BeforeTest
    fun setUp() = runBlocking { server.start() }

    @AfterTest
    fun tearDown() = runBlocking { server.stop() }

    @Test
    fun `tools and annotations`() = runBlocking {
        val tools = server.listTools().associateBy { it.name }

        assertEquals(setOf("list_shared_preferences", "get_shared_preference_values", "set_shared_preference_value"), tools.keys)
        assertEquals(true, tools.getValue("list_shared_preferences").annotations?.readOnlyHint)
        assertEquals(true, tools.getValue("get_shared_preference_values").annotations?.readOnlyHint)
        assertEquals(false, tools.getValue("set_shared_preference_value").annotations?.readOnlyHint)
        assertEquals(true, tools.getValue("set_shared_preference_value").annotations?.destructiveHint)
        // a typed schema: some clients (ex: Gemini) reject a property without type
        val valueSchema = tools.getValue("set_shared_preference_value").inputSchema.properties!!["value"]!!.jsonObject
        assertEquals("string", valueSchema["type"]!!.jsonPrimitive.content)
    }

    @Test
    fun `set_shared_preference_value of a string_set`() = runBlocking {
        setValue("tags", "[\"x\",\"y\"]").json()

        assertEquals(listOf<Value>(Value.StringSetValue(setOf("x", "y"))), repository.edits.map { it.third })
    }

    @Test
    fun `set_shared_preference_value to the current value is confirmed`() = runBlocking {
        val result = setValue("launch_count", 12).json()

        assertEquals(true, result["confirmed"]!!.jsonPrimitive.boolean)
        assertEquals(12, result["newValue"]!!.jsonPrimitive.int)
    }

    @Test
    fun `set_shared_preference_value is not confirmed by an unrelated change`() = runBlocking {
        repository.appAnswersEdits = false
        repository.changeOtherKeyOnEdit = true

        val result = setValue("launch_count", 13).json()

        assertEquals(false, result["confirmed"]!!.jsonPrimitive.boolean)
        assertEquals(null, result["newValue"])
    }

    @Test
    fun `set_shared_preference_value infers the type and returns the previous value`() = runBlocking {
        val result = setValue("launch_count", 13).json()

        assertEquals(listOf(Triple("settings", "launch_count", Value.IntValue(13) as Value)), repository.edits)
        assertEquals("int", result["type"]!!.jsonPrimitive.content)
        assertEquals(12, result["previousValue"]!!.jsonPrimitive.int)
        assertEquals(13, result["newValue"]!!.jsonPrimitive.int)
        assertEquals(true, result["confirmed"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `set_shared_preference_value accepts a stringified value of the type`() = runBlocking {
        setValue("onboarded", "false").json()
        setValue("launch_count", "14").json()

        assertEquals(
            listOf(Value.BooleanValue(false), Value.IntValue(14)),
            repository.edits.map { it.third },
        )
    }

    @Test
    fun `set_shared_preference_value with an explicit type`() = runBlocking {
        setValue("launch_count", 3_000_000_000L, type = "long").json()

        assertEquals(listOf<Value>(Value.LongValue(3_000_000_000L)), repository.edits.map { it.third })
    }

    @Test
    fun `set_shared_preference_value refuses an unknown key`() = runBlocking {
        val result = setValue("them", "light")

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("them"), result.text())
        assertTrue(result.text().contains("get_shared_preference_values"), result.text())
        assertTrue(repository.edits.isEmpty())
    }

    @Test
    fun `set_shared_preference_value refuses a value of the wrong type`() = runBlocking {
        val calls = listOf(
            setValue("launch_count", 3_000_000_000L),
            setValue("launch_count", 1.5),
            setValue("onboarded", "yes"),
            setValue("tags", "a"),
            setValue("theme", 12, type = "unknown"),
        )

        calls.forEach { assertEquals(true, it.isError, it.text()) }
        assertTrue(repository.edits.isEmpty())
    }

    @Test
    fun `set_shared_preference_value of an unknown store`() = runBlocking {
        val result = setValue("theme", "light", sharedPreferenceId = "nope")

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("list_shared_preferences"), result.text())
        assertTrue(repository.edits.isEmpty())
    }

    @Test
    fun `set_shared_preference_value when the app does not answer`() = runBlocking {
        repository.appAnswersEdits = false

        val result = setValue("launch_count", 13).json()

        assertEquals(false, result["confirmed"]!!.jsonPrimitive.boolean)
        assertEquals(12, result["previousValue"]!!.jsonPrimitive.int)
        assertEquals(null, result["newValue"])
        assertEquals(1, repository.edits.size)
    }

    private suspend fun setValue(key: String, value: Any, type: String? = null, sharedPreferenceId: String = "settings") = server.callTool(
        "set_shared_preference_value",
        buildMap {
            put("sharedPreferenceId", sharedPreferenceId)
            put("key", key)
            put("value", value)
            type?.let { put("type", it) }
        },
    )

    @Test
    fun `list_shared_preferences asks the app`() = runBlocking {
        val result = server.callTool("list_shared_preferences").json()

        assertEquals(1, repository.askedForFiles)
        val files = result["sharedPreferences"]!!.jsonArray
        assertEquals(listOf("settings"), files.map { it.jsonObject["sharedPreferenceId"]!!.jsonPrimitive.content })
        assertEquals(listOf("DataStore"), files.map { it.jsonObject["type"]!!.jsonPrimitive.content })
    }

    @Test
    fun `get_shared_preference_values returns the typed values as stored`() = runBlocking {
        val result = server.callTool("get_shared_preference_values", mapOf("sharedPreferenceId" to "settings")).json()

        assertEquals(4, result["total"]!!.jsonPrimitive.int)
        assertEquals(false, result["hasMore"]!!.jsonPrimitive.boolean)
        val entries = result["entries"]!!.jsonArray.map { it.jsonObject }
        assertEquals(listOf("launch_count", "onboarded", "tags", "theme"), entries.map { it["key"]!!.jsonPrimitive.content })
        assertEquals(12, entries[0]["value"]!!.jsonPrimitive.int)
        assertEquals("int", entries[0]["type"]!!.jsonPrimitive.content)
        assertEquals("string_set", entries[2]["type"]!!.jsonPrimitive.content)
        assertEquals(listOf("a", "b"), entries[2]["value"]!!.jsonArray.map { it.jsonPrimitive.content })
        assertEquals("dark", entries[3]["value"]!!.jsonPrimitive.content)
    }

    @Test
    fun `get_shared_preference_values filters and limits`() = runBlocking {
        assertEquals(
            listOf("theme"),
            server.callTool("get_shared_preference_values", mapOf("sharedPreferenceId" to "settings", "text" to "DARK")).json().keys(),
        )
        assertEquals(
            listOf("launch_count"),
            server.callTool("get_shared_preference_values", mapOf("sharedPreferenceId" to "settings", "text" to "count")).json().keys(),
        )
        val limited = server.callTool("get_shared_preference_values", mapOf("sharedPreferenceId" to "settings", "limit" to 1)).json()
        assertEquals(1, limited["returned"]!!.jsonPrimitive.int)
        assertEquals(4, limited["total"]!!.jsonPrimitive.int)
        assertEquals(true, limited["hasMore"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `get_shared_preference_values of an unknown file is empty`() = runBlocking {
        val result = server.callTool("get_shared_preference_values", mapOf("sharedPreferenceId" to "nope")).json()

        assertEquals(0, result["total"]!!.jsonPrimitive.int)
    }

    private fun JsonObject.keys(): List<String> = this["entries"]!!.jsonArray.map { it.jsonObject["key"]!!.jsonPrimitive.content }
}

/**
 * Like the real one, the values are only known once the app answered the request
 */
private class FakeSharedPreferencesRepository(
    private val files: List<DeviceSharedPreferenceDomainModel>,
    private val values: Map<DeviceSharedPreferenceId, List<SharedPreferenceRowDomainModel>>,
) : SharedPreferencesRepository {

    var askedForFiles = 0
    var appAnswersEdits = true
    var changeOtherKeyOnEdit = false
    val edits = mutableListOf<Triple<DeviceSharedPreferenceId, String, Value>>()
    private val knownFiles = MutableStateFlow<List<DeviceSharedPreferenceDomainModel>>(emptyList())
    private val knownValues = MutableStateFlow<Map<DeviceSharedPreferenceId, List<SharedPreferenceRowDomainModel>>>(emptyMap())

    override suspend fun askForDeviceSharedPreferences(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        askedForFiles++
        knownFiles.value = files
    }

    override suspend fun getDeviceSharedPreferencesValues(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ) {
        values[sharedPreferenceId]?.let { rows -> knownValues.value = knownValues.value + (sharedPreferenceId to rows) }
    }

    override fun observeDeviceSharedPreferences(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeviceSharedPreferenceDomainModel>> = knownFiles

    override fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ): Flow<List<SharedPreferenceRowDomainModel>> = knownValues.map { it[sharedPreferenceId] ?: emptyList() }

    override fun observeSelectedDeviceSharedPreference(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DeviceSharedPreferenceDomainModel?> = flowOf(null)

    override fun selectDeviceSharedPreference(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, sharedPreferenceId: DeviceSharedPreferenceId) = error("not used")

    override suspend fun registerDeviceSharedPreferences(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferences: List<DeviceSharedPreferenceDomainModel>,
    ) = error("not used")

    override suspend fun editSharedPrefField(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreference: DeviceSharedPreferenceDomainModel,
        key: String,
        value: Value,
    ) {
        edits += Triple(sharedPreference.id, key, value)
        if (changeOtherKeyOnEdit) {
            val rows = knownValues.value[sharedPreference.id].orEmpty().map { if (it.key == "theme") it.copy(value = Value.StringValue("light")) else it }
            knownValues.value = knownValues.value + (sharedPreference.id to rows)
        }
        // like the app: it answers a set with the full content
        if (appAnswersEdits) {
            val rows = knownValues.value[sharedPreference.id].orEmpty().map { if (it.key == key) it.copy(value = value) else it }
            knownValues.value = knownValues.value + (sharedPreference.id to rows)
        }
    }

    override suspend fun removeSharedPrefField(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreference: DeviceSharedPreferenceDomainModel,
        key: String,
    ) = error("not used")
}
