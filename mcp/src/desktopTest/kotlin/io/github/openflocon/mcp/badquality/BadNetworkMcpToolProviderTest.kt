package io.github.openflocon.mcp.badquality

import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigId
import io.github.openflocon.domain.network.repository.NetworkBadQualityRepository
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.targetResolver
import io.github.openflocon.mcp.text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class BadNetworkMcpToolProviderTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val sample = DeviceIdAndPackageNameDomainModel(deviceId = "pixel", packageName = "io.flocon.sample", appInstance = 2)

    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample", lastAppInstance = 2), app("io.flocon.other"))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
        activeDevicesValue = setOf(sample),
    )
    private val repository = FakeBadQualityRepository()

    private val server = McpTestServer(
        toolProviders = listOf(
            BadNetworkMcpToolProvider(
                networkBadQualityRepository = repository,
                devicesRepository = devicesRepository,
                targetResolver = targetResolver(devicesRepository),
            ),
        ),
    )

    @BeforeTest
    fun setUp() = runBlocking { server.start() }

    @AfterTest
    fun tearDown() = runBlocking { server.stop() }

    @Test
    fun `only the list tool is flagged read only`() = runBlocking {
        val readOnlyTools = server.listTools().filter { it.annotations?.readOnlyHint == true }.map { it.name }

        assertEquals(listOf("list_bad_network_configs"), readOnlyTools)
    }

    @Test
    fun `create a slow and flaky config, active right away`() = runBlocking {
        val result = server.callTool(
            "create_bad_network_config",
            mapOf(
                "name" to "Flaky",
                "latencyTriggerProbability" to 0.5,
                "minLatencyMs" to 1000,
                "maxLatencyMs" to 3000,
                "errorProbability" to 0.3,
                "errors" to listOf(
                    mapOf("exceptionClassPath" to "java.net.SocketTimeoutException", "weight" to 2),
                    mapOf("httpCode" to 503, "body" to "down"),
                ),
                "enabled" to true,
            ),
        ).json()

        val stored = repository.configsOf(sample).single()
        assertEquals("Flaky", stored.name)
        assertTrue(stored.isEnabled)
        assertEquals(BadQualityConfigDomainModel.LatencyConfig(0.5, 1000, 3000), stored.latency)
        assertEquals(0.3, stored.errorProbability)
        assertEquals(
            listOf(
                BadQualityConfigDomainModel.Error(2f, BadQualityConfigDomainModel.Error.Type.Exception("java.net.SocketTimeoutException")),
                BadQualityConfigDomainModel.Error(1f, BadQualityConfigDomainModel.Error.Type.Body(503, "down", "application/json")),
            ),
            stored.errors,
        )
        assertEquals(stored.id, result["config"]!!.jsonObject["configId"]!!.jsonPrimitive.content)
        // sent to the connected app
        assertEquals(stored, repository.sentToDevice[sample])
    }

    @Test
    fun `a created config is not active by default`() = runBlocking {
        server.callTool("create_bad_network_config", mapOf("name" to "Slow", "latencyTriggerProbability" to 1, "maxLatencyMs" to 500)).json()

        assertTrue(repository.configsOf(sample).none { it.isEnabled })
        assertNull(repository.sentToDevice[sample])
    }

    @Test
    fun `list the configs of the app`() = runBlocking {
        repository.put(sample, config("a", enabled = true))
        repository.put(sample, config("b"))

        val configs = server.callTool("list_bad_network_configs").json()["configs"]!!.jsonArray

        assertEquals(listOf("a", "b"), configs.map { it.jsonObject["configId"]!!.jsonPrimitive.content })
        assertEquals("true", configs.first().jsonObject["isEnabled"]!!.jsonPrimitive.content)
    }

    @Test
    fun `activating a config deactivates the others, null turns the simulation off`() = runBlocking {
        repository.put(sample, config("a", enabled = true))
        repository.put(sample, config("b"))

        server.callTool("set_active_bad_network_config", mapOf("configId" to "b")).json()
        assertEquals(listOf("b"), repository.configsOf(sample).filter { it.isEnabled }.map { it.id })
        assertEquals("b", repository.sentToDevice[sample]?.id)

        server.callTool("set_active_bad_network_config").json()
        assertTrue(repository.configsOf(sample).none { it.isEnabled })
        assertTrue(sample in repository.sentToDevice && repository.sentToDevice[sample] == null)
    }

    @Test
    fun `update only changes the given arguments`() = runBlocking {
        repository.put(sample, config("a", enabled = true))

        server.callTool("update_bad_network_config", mapOf("configId" to "a", "maxLatencyMs" to 9000)).json()

        val stored = repository.configsOf(sample).single()
        assertEquals(BadQualityConfigDomainModel.LatencyConfig(1.0, 100, 9000), stored.latency)
        assertEquals("a", stored.name)
        assertTrue(stored.isEnabled)
        // the active config is sent again
        assertEquals(stored, repository.sentToDevice[sample])
    }

    @Test
    fun `delete a config`() = runBlocking {
        repository.put(sample, config("a", enabled = true))

        server.callTool("delete_bad_network_config", mapOf("configId" to "a")).json()

        assertTrue(repository.configsOf(sample).isEmpty())
        assertNull(repository.sentToDevice[sample])
    }

    @Test
    fun `invalid arguments are rejected`() = runBlocking {
        fun create(vararg args: Pair<String, Any?>) = mapOf<String, Any?>("name" to "x") + args

        val invalid = listOf(
            create("latencyTriggerProbability" to 1.5),
            create("minLatencyMs" to 500, "maxLatencyMs" to 100),
            // error probability without any error
            create("errorProbability" to 0.5),
            create("errorProbability" to 0.5, "errors" to listOf(mapOf("weight" to 1))),
            create("errorProbability" to 0.5, "errors" to listOf(mapOf("httpCode" to 99))),
            create(
                "errorProbability" to 0.5,
                "errors" to listOf(mapOf("httpCode" to 500, "exceptionClassPath" to "java.io.IOException")),
            ),
        )
        invalid.forEach { arguments ->
            val result = server.callTool("create_bad_network_config", arguments)
            assertEquals(true, result.isError, "$arguments: ${result.text()}")
        }
        assertTrue(repository.configsOf(sample).isEmpty())
    }

    @Test
    fun `unknown config id`() = runBlocking {
        val result = server.callTool("set_active_bad_network_config", mapOf("configId" to "nope"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("list_bad_network_configs"))
    }

    private fun config(id: String, enabled: Boolean = false) = BadQualityConfigDomainModel(
        id = id,
        isEnabled = enabled,
        name = id,
        createdAt = Instant.fromEpochMilliseconds(0),
        latency = BadQualityConfigDomainModel.LatencyConfig(triggerProbability = 1.0, minLatencyMs = 100, maxLatencyMs = 200),
        errorProbability = 0.0,
        errors = emptyList(),
    )
}

private class FakeBadQualityRepository : NetworkBadQualityRepository {

    private val configs = MutableStateFlow<List<Pair<Pair<String, String>, BadQualityConfigDomainModel>>>(emptyList())
    val sentToDevice = mutableMapOf<DeviceIdAndPackageNameDomainModel, BadQualityConfigDomainModel?>()

    private fun DeviceIdAndPackageNameDomainModel.key() = deviceId to packageName

    fun configsOf(target: DeviceIdAndPackageNameDomainModel) = configs.value.filter { it.first == target.key() }.map { it.second }

    fun put(target: DeviceIdAndPackageNameDomainModel, config: BadQualityConfigDomainModel) {
        configs.value = configs.value.filterNot { it.second.id == config.id } + (target.key() to config)
    }

    override suspend fun setupBadNetworkQuality(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, config: BadQualityConfigDomainModel?) {
        sentToDevice[deviceIdAndPackageName] = config
    }

    override suspend fun saveBadNetworkQuality(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, config: BadQualityConfigDomainModel) = put(deviceIdAndPackageName, config)

    override suspend fun getNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId,
    ): BadQualityConfigDomainModel? = configsOf(deviceIdAndPackageName).firstOrNull { it.id == configId }

    override suspend fun getTheOnlyEnabledNetworkQuality(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = configsOf(deviceIdAndPackageName).firstOrNull { it.isEnabled }

    override suspend fun deleteNetworkQuality(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, configId: BadQualityConfigId) {
        configs.value = configs.value.filterNot { it.first == deviceIdAndPackageName.key() && it.second.id == configId }
    }

    override fun observeNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId,
    ): Flow<BadQualityConfigDomainModel?> = configs.map { configsOf(deviceIdAndPackageName).firstOrNull { it.id == configId } }

    override fun observeAllNetworkQualities(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<BadQualityConfigDomainModel>> = configs.map { configsOf(deviceIdAndPackageName) }

    override suspend fun setEnabledConfig(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, configId: BadQualityConfigId?) {
        configs.value = configs.value.map { (key, config) ->
            if (key == deviceIdAndPackageName.key()) key to config.copy(isEnabled = config.id == configId) else key to config
        }
    }
}
