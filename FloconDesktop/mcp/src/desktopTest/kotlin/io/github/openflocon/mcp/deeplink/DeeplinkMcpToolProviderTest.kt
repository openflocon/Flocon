package io.github.openflocon.mcp.deeplink

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.deeplink.models.DeeplinkVariableDomainModel
import io.github.openflocon.domain.deeplink.models.Deeplinks
import io.github.openflocon.domain.deeplink.repository.DeeplinkRepository
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.targetResolver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeeplinkMcpToolProviderTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample"))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
    )

    private val profile = DeeplinkDomainModel(
        id = 1,
        label = "Profile",
        link = "app://profile/[userId]?tab=[tab]",
        description = "Opens a profile",
        parameters = listOf(
            DeeplinkDomainModel.Parameter.Variable(name = "userId", variableName = "currentUser"),
            DeeplinkDomainModel.Parameter.AutoComplete(name = "tab", autoComplete = listOf("posts", "likes")),
        ),
    )

    private val opened = mutableListOf<Triple<String, Long, DeviceIdAndPackageNameDomainModel>>()
    private var openResult: Either<Throwable, String> = Success("Starting: Intent")
    private val opener = DeeplinkOpener { link, deeplinkId, _, target ->
        opened += Triple(link, deeplinkId, target)
        openResult
    }

    private val server = McpTestServer(
        toolProviders = listOf(
            DeeplinkMcpToolProvider(
                deeplinkRepository = FakeDeeplinkRepository(
                    deeplinks = Deeplinks(
                        deeplinks = listOf(profile),
                        variables = listOf(
                            DeeplinkVariableDomainModel("currentUser", DeeplinkVariableDomainModel.Mode.Input, "The user id"),
                            DeeplinkVariableDomainModel("env", DeeplinkVariableDomainModel.Mode.AutoComplete(listOf("dev", "prod")), null),
                        ),
                    ),
                    history = listOf(profile.copy(id = 2, link = "app://profile/42?tab=posts", parameters = emptyList())),
                ),
                deeplinkOpener = opener,
                targetResolver = targetResolver(devicesRepository),
            ),
        ),
    )

    @BeforeTest
    fun setUp() = runBlocking { server.start() }

    @AfterTest
    fun tearDown() = runBlocking { server.stop() }

    @Test
    fun `only open_deeplink is not read only`() = runBlocking {
        val tools = server.listTools()

        assertEquals(setOf("list_deeplinks", "list_deeplink_history", "open_deeplink"), tools.map { it.name }.toSet())
        assertEquals(listOf("open_deeplink"), tools.filter { it.annotations?.readOnlyHint != true }.map { it.name })
    }

    @Test
    fun `open_deeplink opens the link on the resolved target`() = runBlocking {
        val result = server.callTool("open_deeplink", mapOf("link" to "app://profile/42?tab=posts", "deeplinkId" to 1)).json()

        val (link, id, target) = opened.single()
        assertEquals("app://profile/42?tab=posts", link)
        assertEquals(1L, id)
        assertEquals("pixel", target.deviceId)
        assertEquals("io.flocon.sample", target.packageName)
        assertEquals("Starting: Intent", result["output"]!!.jsonPrimitive.content)
    }

    @Test
    fun `open_deeplink returns an error when the deeplink cannot be opened`() = runBlocking {
        openResult = Failure(IllegalStateException("No device"))

        val result = server.callTool("open_deeplink", mapOf("link" to "app://home"))

        assertEquals(true, result.isError)
    }

    @Test
    fun `list_deeplinks returns the deeplinks, parameters and variables`() = runBlocking {
        val result = server.callTool("list_deeplinks").json()

        val deeplink = result["deeplinks"]!!.jsonArray.single().jsonObject
        assertEquals("app://profile/[userId]?tab=[tab]", deeplink["link"]!!.jsonPrimitive.content)
        val parameters = deeplink["parameters"]!!.jsonArray.map { it.jsonObject }
        assertEquals(listOf("variable", "auto_complete"), parameters.map { it["type"]!!.jsonPrimitive.content })
        assertEquals("currentUser", parameters[0]["variableName"]!!.jsonPrimitive.content)
        assertEquals(listOf("posts", "likes"), parameters[1]["suggestions"]!!.jsonArray.map { it.jsonPrimitive.content })

        val variables = result["variables"]!!.jsonArray.map { it.jsonObject }
        assertEquals(listOf("input", "auto_complete"), variables.map { it["mode"]!!.jsonPrimitive.content })
        assertEquals(listOf("dev", "prod"), variables[1]["suggestions"]!!.jsonArray.map { it.jsonPrimitive.content })
    }

    @Test
    fun `list_deeplink_history returns the opened deeplinks`() = runBlocking {
        val result = server.callTool("list_deeplink_history").json()

        assertEquals(
            listOf("app://profile/42?tab=posts"),
            result["deeplinks"]!!.jsonArray.map { it.jsonObject["link"]!!.jsonPrimitive.content },
        )
    }
}

private class FakeDeeplinkRepository(
    private val deeplinks: Deeplinks,
    private val history: List<DeeplinkDomainModel>,
) : DeeplinkRepository {
    override fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<Deeplinks> = flowOf(deeplinks)

    override suspend fun getDeeplinkById(deeplinkId: Long, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): DeeplinkDomainModel? = error("not used")

    override fun observeHistory(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>> = flowOf(history)

    override suspend fun addToHistory(item: DeeplinkDomainModel, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = error("not used")

    override suspend fun removeFromHistory(deeplinkId: Long, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = error("not used")
}
