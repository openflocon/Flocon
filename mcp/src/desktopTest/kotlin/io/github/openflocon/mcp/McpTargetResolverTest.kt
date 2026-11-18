package io.github.openflocon.mcp

import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class McpTargetResolverTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val iphone = DeviceDomainModel(deviceId = "iphone", deviceName = "iPhone 17", platform = "ios")

    private fun repository(
        currentDeviceId: String? = "pixel",
        selectedApps: Map<String, String> = mapOf("pixel" to "io.flocon.sample"),
    ) = FakeDevicesRepository(
        devicesWithApps = mapOf(
            pixel to listOf(app("io.flocon.sample", lastAppInstance = 10), app("io.flocon.other", lastAppInstance = 11)),
            iphone to listOf(app("io.flocon.sample", lastAppInstance = 20)),
        ),
        currentDeviceIdValue = currentDeviceId,
        selectedApps = selectedApps,
    )

    @Test
    fun `without arguments, the app selected in Flocon is used`() = runBlocking {
        val target = targetResolver(repository()).resolve(deviceId = null, packageName = null)

        assertEquals(DeviceIdAndPackageNameDomainModel("pixel", "io.flocon.sample", 10), target)
    }

    @Test
    fun `explicit device and app`() = runBlocking {
        val target = targetResolver(repository()).resolve(deviceId = "iphone", packageName = "io.flocon.sample")

        assertEquals(DeviceIdAndPackageNameDomainModel("iphone", "io.flocon.sample", 20), target)
    }

    @Test
    fun `package name only, the current device is preferred`() = runBlocking {
        val target = targetResolver(repository()).resolve(deviceId = null, packageName = "io.flocon.sample")

        assertEquals("pixel", target.deviceId)
    }

    @Test
    fun `package name only, falls back on the most recent session`() = runBlocking {
        val target = targetResolver(repository(currentDeviceId = null)).resolve(deviceId = null, packageName = "io.flocon.sample")

        assertEquals("iphone", target.deviceId)
    }

    @Test
    fun `device only, with a single app`() = runBlocking {
        val target = targetResolver(repository(selectedApps = emptyMap())).resolve(deviceId = "iphone", packageName = null)

        assertEquals(DeviceIdAndPackageNameDomainModel("iphone", "io.flocon.sample", 20), target)
    }

    @Test
    fun `device only, with several apps and none selected`() {
        val error = assertFailsWith<McpToolException> {
            runBlocking { targetResolver(repository(selectedApps = emptyMap())).resolve(deviceId = "pixel", packageName = null) }
        }
        assertTrue(error.message!!.contains("io.flocon.other"))
    }

    @Test
    fun `nothing selected, the error lists the available apps`() {
        val error = assertFailsWith<McpToolException> {
            runBlocking { targetResolver(repository(currentDeviceId = null)).resolve(deviceId = null, packageName = null) }
        }
        assertTrue(error.message!!.contains("(iphone, io.flocon.sample)"))
    }

    @Test
    fun `unknown app`() {
        assertFailsWith<McpToolException> {
            runBlocking { targetResolver(repository()).resolve(deviceId = "pixel", packageName = "unknown") }
        }
    }

    @Test
    fun `all targets start with the current one`() = runBlocking {
        val targets = targetResolver(repository()).allTargets()

        assertEquals(3, targets.size)
        assertEquals(DeviceIdAndPackageNameDomainModel("pixel", "io.flocon.sample", 10), targets.first())
    }
}
