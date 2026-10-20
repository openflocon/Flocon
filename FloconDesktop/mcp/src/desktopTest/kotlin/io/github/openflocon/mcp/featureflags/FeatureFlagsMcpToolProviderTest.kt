package io.github.openflocon.mcp.featureflags

import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.featureflags.models.FeatureFlagDomainModel
import io.github.openflocon.domain.featureflags.repository.FeatureFlagsRepository
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.targetResolver
import io.github.openflocon.mcp.text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FeatureFlagsMcpToolProviderTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample"))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
    )
    private val longPayload = "{\"value\":\"" + "x".repeat(300) + "\"}"
    private val featureFlagsRepository = FakeFeatureFlagsRepository(
        flags = listOf(
            FeatureFlagDomainModel(provider = "firebase", name = "dark_mode", variant = "on", payload = null),
            FeatureFlagDomainModel(provider = "firebase", name = "new_checkout", variant = "treatment", payload = longPayload),
            FeatureFlagDomainModel(provider = "statsig", name = "search_ranking", variant = "variant_b", payload = "{\"algorithm\":\"bm25\"}"),
        ),
    )

    private val server = McpTestServer(
        toolProviders = listOf(
            FeatureFlagsMcpToolProvider(
                featureFlagsRepository = featureFlagsRepository,
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

        assertEquals(setOf("list_feature_flags", "get_feature_flag"), tools.map { it.name }.toSet())
        assertTrue(tools.all { it.annotations?.readOnlyHint == true })
    }

    @Test
    fun `list_feature_flags returns the flags, the providers and truncates the payloads`() = runBlocking {
        val result = server.callTool("list_feature_flags").json()

        assertEquals(listOf("firebase", "statsig"), result["providers"]!!.jsonArray.map { it.jsonPrimitive.content })
        val flags = result["flags"]!!.jsonArray.map { it.jsonObject }
        assertEquals(listOf("dark_mode", "new_checkout", "search_ranking"), flags.map { it["name"]!!.jsonPrimitive.content })
        assertEquals(true, flags[1]["payloadTruncated"]!!.jsonPrimitive.boolean)
        assertEquals(200, flags[1]["payload"]!!.jsonPrimitive.content.length)
        assertEquals(false, result["hasMore"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `list_feature_flags filters by provider and query`() = runBlocking {
        val byProvider = server.callTool("list_feature_flags", mapOf("provider" to "statsig")).json()
        assertEquals(listOf("search_ranking"), byProvider["flags"]!!.jsonArray.map { it.jsonObject["name"]!!.jsonPrimitive.content })

        val byQuery = server.callTool("list_feature_flags", mapOf("query" to "treat")).json()
        assertEquals(listOf("new_checkout"), byQuery["flags"]!!.jsonArray.map { it.jsonObject["name"]!!.jsonPrimitive.content })
    }

    @Test
    fun `list_feature_flags reports hasMore`() = runBlocking {
        val result = server.callTool("list_feature_flags", mapOf("limit" to 2)).json()

        assertEquals(2, result["flags"]!!.jsonArray.size)
        assertEquals(true, result["hasMore"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `get_feature_flag returns the full payload`() = runBlocking {
        val result = server.callTool("get_feature_flag", mapOf("provider" to "firebase", "name" to "new_checkout")).json()

        val flag = result["flag"]!!.jsonObject
        assertEquals("treatment", flag["variant"]!!.jsonPrimitive.content)
        assertEquals(longPayload, flag["payload"]!!.jsonPrimitive.content)
    }

    @Test
    fun `get_feature_flag with an unknown flag is an error`() = runBlocking {
        val result = server.callTool("get_feature_flag", mapOf("provider" to "firebase", "name" to "nope"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("nope"))
    }
}

private class FakeFeatureFlagsRepository(
    private val flags: List<FeatureFlagDomainModel>,
) : FeatureFlagsRepository {

    override fun observeFeatureFlags(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
        filter: String?,
    ): Flow<List<FeatureFlagDomainModel>> = error("not used")

    override fun observeProviders(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<String>> = error("not used")

    override fun observeSelectedProvider(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<String?> = error("not used")

    override suspend fun selectProvider(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
    ) = error("not used")

    override fun observeFeatureFlag(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
        name: String,
    ): Flow<FeatureFlagDomainModel?> = error("not used")

    override suspend fun searchFeatureFlags(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String?,
        filter: String?,
        limit: Int,
    ): List<FeatureFlagDomainModel> = flags
        .filter { provider == null || it.provider == provider }
        .filter {
            filter == null ||
                it.name.contains(filter, ignoreCase = true) ||
                it.variant.contains(filter, ignoreCase = true) ||
                it.payload?.contains(filter, ignoreCase = true) == true
        }
        .take(limit)

    override suspend fun getFeatureFlag(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
        name: String,
    ): FeatureFlagDomainModel? = flags.firstOrNull { it.provider == provider && it.name == name }

    override suspend fun getProviders(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<String> = flags.map { it.provider }.distinct()
}
