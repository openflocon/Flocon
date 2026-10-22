package io.github.openflocon.mcp.mocks

import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.models.NetworkWebsocketId
import io.github.openflocon.domain.network.repository.NetworkMocksRepository
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.FakeNetworkRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.httpCall
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.targetResolver
import io.github.openflocon.mcp.text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NetworkMocksMcpToolProviderTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val sample = DeviceIdAndPackageNameDomainModel(deviceId = "pixel", packageName = "io.flocon.sample", appInstance = 2)
    private val other = DeviceIdAndPackageNameDomainModel(deviceId = "pixel", packageName = "io.flocon.other", appInstance = 1)

    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample", lastAppInstance = 2), app("io.flocon.other"))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
        activeDevicesValue = setOf(sample),
    )
    private val networkRepository = FakeNetworkRepository(
        calls = mapOf(
            ("pixel" to "io.flocon.sample") to listOf(
                httpCall("users", url = "https://api.flocon.io/users?page=1", startTime = 10, appInstance = 2),
            ),
        ),
    )
    private val mocksRepository = FakeNetworkMocksRepository()

    private val server = McpTestServer(
        toolProviders = listOf(
            NetworkMocksMcpToolProvider(
                networkMocksRepository = mocksRepository,
                networkRepository = networkRepository,
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
    fun `only the read tools are flagged read only`() = runBlocking {
        val readOnlyTools = server.listTools().filter { it.annotations?.readOnlyHint == true }.map { it.name }

        assertEquals(listOf("list_network_mocks", "get_network_mock"), readOnlyTools)
    }

    @Test
    fun `create a mock from scratch`() = runBlocking {
        val mock = server.callTool(
            "create_network_mock",
            mapOf(
                "urlPattern" to ".*/login",
                "method" to "post",
                "httpCode" to 500,
                "body" to """{"error":"boom"}""",
                "headers" to mapOf("X-Mocked" to "true"),
                "delayMs" to 300,
            ),
        ).json()["mock"]!!.jsonObject

        val stored = mocksRepository.mocksOf(sample).single()
        assertEquals(mock["mockId"]!!.jsonPrimitive.content, stored.id)
        assertEquals(MockNetworkDomainModel.Expectation(urlPattern = ".*/login", method = "POST"), stored.expectation)
        assertEquals(
            MockNetworkDomainModel.Response.Body(
                httpCode = 500,
                body = """{"error":"boom"}""",
                mediaType = "application/json",
                delay = 300,
                headers = mapOf("X-Mocked" to "true"),
            ),
            stored.response,
        )
        assertTrue(stored.isEnabled)
        // sent to the connected app
        assertEquals(listOf(stored), mocksRepository.sentToDevice[sample])
    }

    @Test
    fun `create a mock from a network call, with overrides`() = runBlocking {
        server.callTool("create_network_mock", mapOf("fromCallId" to "users", "httpCode" to 503)).json()

        val stored = mocksRepository.mocksOf(sample).single()
        assertEquals(Regex.escape("https://api.flocon.io/users?page=1"), stored.expectation.urlPattern)
        assertTrue(Regex(stored.expectation.urlPattern).matches("https://api.flocon.io/users?page=1"))
        assertEquals("GET", stored.expectation.method)
        val response = stored.response as MockNetworkDomainModel.Response.Body
        assertEquals(503, response.httpCode)
        assertEquals("""{"name":"flocon"}""", response.body)
    }

    @Test
    fun `create a mock throwing an exception`() = runBlocking {
        server.callTool(
            "create_network_mock",
            mapOf("urlPattern" to ".*", "exceptionClassPath" to "java.net.SocketTimeoutException"),
        ).json()

        assertEquals(
            MockNetworkDomainModel.Response.Exception(delay = 0, classPath = "java.net.SocketTimeoutException"),
            mocksRepository.mocksOf(sample).single().response,
        )
    }

    @Test
    fun `create a disabled mock`() = runBlocking {
        val result = server.callTool("create_network_mock", mapOf("urlPattern" to ".*", "enabled" to false)).json()

        assertTrue(result["message"]!!.jsonPrimitive.content.contains("disabled"))
        assertFalse(mocksRepository.mocksOf(sample).single().isEnabled)
        assertEquals(emptyList(), mocksRepository.sentToDevice[sample])
    }

    @Test
    fun `invalid arguments are errors`() = runBlocking {
        val invalidRegex = server.callTool("create_network_mock", mapOf("urlPattern" to "[a-"))
        assertEquals(true, invalidRegex.isError)
        assertTrue(invalidRegex.text().contains("regex"))

        val noUrl = server.callTool("create_network_mock", mapOf("httpCode" to 200))
        assertEquals(true, noUrl.isError)

        val bodyAndException = server.callTool(
            "create_network_mock",
            mapOf("urlPattern" to ".*", "httpCode" to 500, "exceptionClassPath" to "java.io.IOException"),
        )
        assertEquals(true, bodyAndException.isError)
        assertTrue(mocksRepository.mocksOf(sample).isEmpty())
    }

    @Test
    fun `list the mocks of the app, including the shared ones`() = runBlocking {
        mocksRepository.addMock(sample, mock("sample-mock"))
        mocksRepository.addMock(other, mock("other-mock"))
        mocksRepository.addMock(other, mock("shared-mock", isShared = true))
        mocksRepository.addMock(sample, mock("disabled-mock", isEnabled = false))

        assertEquals(
            setOf("sample-mock", "shared-mock", "disabled-mock"),
            server.callTool("list_network_mocks").json().mockIds().toSet(),
        )
        assertEquals(
            setOf("sample-mock", "shared-mock"),
            server.callTool("list_network_mocks", mapOf("onlyEnabled" to true)).json().mockIds().toSet(),
        )
        assertEquals(
            setOf("other-mock", "shared-mock"),
            server.callTool("list_network_mocks", mapOf("packageName" to "io.flocon.other")).json().mockIds().toSet(),
        )
    }

    @Test
    fun `get a mock with its body`() = runBlocking {
        mocksRepository.addMock(sample, mock("sample-mock"))

        val result = server.callTool("get_network_mock", mapOf("mockId" to "sample-mock")).json()

        val response = result["response"]!!.jsonObject
        assertEquals(404, response["httpCode"]!!.jsonPrimitive.int)
        assertEquals("not found", response["body"]!!.jsonObject["content"]!!.jsonPrimitive.content)
    }

    @Test
    fun `update a mock keeps it on its app`() = runBlocking {
        mocksRepository.addMock(other, mock("other-mock"))

        server.callTool("update_network_mock", mapOf("mockId" to "other-mock", "enabled" to false, "httpCode" to 418)).json()

        val updated = mocksRepository.mocksOf(other).single()
        assertFalse(updated.isEnabled)
        assertEquals(418, (updated.response as MockNetworkDomainModel.Response.Body).httpCode)
        assertEquals("not found", (updated.response as MockNetworkDomainModel.Response.Body).body)
        assertTrue(mocksRepository.mocksOf(sample).isEmpty())
    }

    @Test
    fun `update a body mock into an exception mock`() = runBlocking {
        mocksRepository.addMock(sample, mock("sample-mock"))

        server.callTool("update_network_mock", mapOf("mockId" to "sample-mock", "exceptionClassPath" to "java.io.IOException")).json()

        assertEquals(
            MockNetworkDomainModel.Response.Exception(delay = 0, classPath = "java.io.IOException"),
            mocksRepository.mocksOf(sample).single().response,
        )
    }

    @Test
    fun `delete a mock`() = runBlocking {
        mocksRepository.addMock(sample, mock("sample-mock"))

        server.callTool("delete_network_mock", mapOf("mockId" to "sample-mock")).json()

        assertTrue(mocksRepository.mocksOf(sample).isEmpty())
        assertEquals(emptyList(), mocksRepository.sentToDevice[sample])

        val unknown = server.callTool("delete_network_mock", mapOf("mockId" to "sample-mock"))
        assertEquals(true, unknown.isError)
    }

    private fun JsonObject.mockIds(): List<String> = this["mocks"]!!.jsonArray.map { it.jsonObject["mockId"]!!.jsonPrimitive.content }

    private fun mock(
        id: String,
        isEnabled: Boolean = true,
        isShared: Boolean = false,
    ) = MockNetworkDomainModel(
        id = id,
        isEnabled = isEnabled,
        expectation = MockNetworkDomainModel.Expectation(urlPattern = ".*/$id", method = "GET"),
        response = MockNetworkDomainModel.Response.Body(
            httpCode = 404,
            body = "not found",
            mediaType = "text/plain",
            delay = 0,
            headers = emptyMap(),
        ),
        isShared = isShared,
        displayName = id,
    )
}

/**
 * Stores the mocks with their app like the Room implementation: shared mocks have no app
 */
private class FakeNetworkMocksRepository : NetworkMocksRepository {

    private val mocks = MutableStateFlow<Map<String, Pair<Pair<String, String>?, MockNetworkDomainModel>>>(emptyMap())
    val sentToDevice = mutableMapOf<DeviceIdAndPackageNameDomainModel, List<MockNetworkDomainModel>>()

    private fun List<Pair<Pair<String, String>?, MockNetworkDomainModel>>.of(target: DeviceIdAndPackageNameDomainModel) = filter { (app, _) -> app == null || app == (target.deviceId to target.packageName) }.map { it.second }

    fun mocksOf(target: DeviceIdAndPackageNameDomainModel): List<MockNetworkDomainModel> = mocks.value.values.toList().of(target)

    override suspend fun addMock(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, mock: MockNetworkDomainModel) {
        val app = if (mock.isShared) null else deviceIdAndPackageName.deviceId to deviceIdAndPackageName.packageName
        mocks.value += mock.id to (app to mock)
    }

    override suspend fun getMock(id: String): MockNetworkDomainModel? = mocks.value[id]?.second

    override suspend fun getAllEnabledMocks(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): List<MockNetworkDomainModel> = mocksOf(deviceIdAndPackageName).filter { it.isEnabled }

    override suspend fun observeAll(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<MockNetworkDomainModel>> = mocks.map { it.values.toList().of(deviceIdAndPackageName) }

    override suspend fun setupMocks(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, mocks: List<MockNetworkDomainModel>) {
        sentToDevice[deviceIdAndPackageName] = mocks
    }

    override suspend fun deleteMock(id: String) {
        mocks.value -= id
    }

    override suspend fun updateMockIsEnabled(id: String, isEnabled: Boolean) = error("not used")
    override suspend fun updateMockDevice(mockId: String, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel?) = error("not used")
    override suspend fun observeWebsocketClientsIds(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<NetworkWebsocketId>> = error("not used")

    override suspend fun sendWebsocketMock(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, websocketId: NetworkWebsocketId, message: String) = error("not used")
}
