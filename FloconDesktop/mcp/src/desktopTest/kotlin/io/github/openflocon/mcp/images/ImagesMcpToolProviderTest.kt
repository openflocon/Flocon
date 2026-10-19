package io.github.openflocon.mcp.images

import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.images.models.DeviceImageDomainModel
import io.github.openflocon.domain.images.repository.ImagesRepository
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.targetResolver
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

class ImagesMcpToolProviderTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample"))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
    )
    private val imagesRepository = FakeImagesRepository(
        listOf(
            DeviceImageDomainModel(url = "https://cdn.io/a.png", time = 10, headers = mapOf("etag" to "1")),
            DeviceImageDomainModel(url = "https://cdn.io/b.jpg", time = 30, headers = emptyMap()),
            DeviceImageDomainModel(url = "https://cdn.io/c.png", time = 20, headers = emptyMap()),
        ),
    )

    private val server = McpTestServer(
        toolProviders = listOf(
            ImagesMcpToolProvider(imagesRepository = imagesRepository, targetResolver = targetResolver(devicesRepository)),
        ),
    )

    @BeforeTest
    fun setUp() = runBlocking { server.start() }

    @AfterTest
    fun tearDown() = runBlocking { server.stop() }

    @Test
    fun `the tool is read only`() = runBlocking {
        val tools = server.listTools()

        assertEquals(listOf("list_images"), tools.map { it.name })
        assertTrue(tools.all { it.annotations?.readOnlyHint == true })
    }

    @Test
    fun `list_images returns the newest first, without headers`() = runBlocking {
        val result = server.callTool("list_images").json()

        assertEquals(listOf("b.jpg", "c.png", "a.png"), result.urls())
        assertEquals(false, result["hasMore"]!!.jsonPrimitive.boolean)
        assertTrue("headers" !in result["images"]!!.jsonArray.first().jsonObject)
    }

    @Test
    fun `list_images filters, orders and limits`() = runBlocking {
        assertEquals(listOf("a.png", "c.png"), server.callTool("list_images", mapOf("text" to ".PNG", "order" to "oldest_first")).json().urls())
        assertEquals(listOf("b.jpg", "c.png"), server.callTool("list_images", mapOf("sinceTimestamp" to 20)).json().urls())

        val limited = server.callTool("list_images", mapOf("limit" to 2, "includeHeaders" to true)).json()
        assertEquals(2, limited["returned"]!!.jsonPrimitive.int)
        assertEquals(true, limited["hasMore"]!!.jsonPrimitive.boolean)
        assertTrue("headers" in limited["images"]!!.jsonArray.first().jsonObject)
    }

    private fun JsonObject.urls(): List<String> =
        this["images"]!!.jsonArray.map { it.jsonObject["url"]!!.jsonPrimitive.content.substringAfterLast('/') }
}

private class FakeImagesRepository(private val images: List<DeviceImageDomainModel>) : ImagesRepository {
    override fun observeImages(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeviceImageDomainModel>> = flowOf(images)
    override suspend fun clearImages(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = error("not used")
}
