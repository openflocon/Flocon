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
        files = listOf(DeviceSharedPreferenceDomainModel(id = "settings", name = "settings")),
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
                answerTimeoutMs = 200,
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

        assertEquals(setOf("list_shared_preferences", "get_shared_preference_values"), tools.map { it.name }.toSet())
        assertTrue(tools.all { it.annotations?.readOnlyHint == true })
    }

    @Test
    fun `list_shared_preferences asks the app`() = runBlocking {
        val result = server.callTool("list_shared_preferences").json()

        assertEquals(1, repository.askedForFiles)
        val files = result["sharedPreferences"]!!.jsonArray
        assertEquals(listOf("settings"), files.map { it.jsonObject["sharedPreferenceId"]!!.jsonPrimitive.content })
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

    override fun observeSelectedDeviceSharedPreference(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DeviceSharedPreferenceDomainModel?> =
        flowOf(null)

    override fun selectDeviceSharedPreference(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, sharedPreferenceId: DeviceSharedPreferenceId) =
        error("not used")

    override suspend fun registerDeviceSharedPreferences(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferences: List<DeviceSharedPreferenceDomainModel>,
    ) = error("not used")

    override suspend fun editSharedPrefField(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreference: DeviceSharedPreferenceDomainModel,
        key: String,
        value: Value,
    ) = error("not used")
}
