package io.github.openflocon.flocondesktop.app.ui.delegates

import io.github.openflocon.domain.device.models.DeviceDomainModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MapperTest {

    private val device = DeviceDomainModel(deviceId = "id", deviceName = "Pixel", platform = "android")

    @Test
    fun `loopback addresses are detected`() {
        listOf("127.0.0.1", "localhost", "::1", "0:0:0:0:0:0:0:1").forEach {
            assertTrue(isLoopbackAddress(it), it)
        }
        assertFalse(isLoopbackAddress("192.168.1.42"))
    }

    @Test
    fun `a lan address is exposed to the ui`() {
        val ui = device.mapToUi(
            activeDevices = emptySet(),
            activeAddresses = mapOf("id" to "192.168.1.42"),
            currentDeviceCapabilities = null,
        )

        assertEquals("192.168.1.42", ui.address)
    }

    @Test
    fun `an adb device has no address`() {
        val ui = device.mapToUi(
            activeDevices = emptySet(),
            activeAddresses = mapOf("id" to "127.0.0.1"),
            currentDeviceCapabilities = null,
        )

        assertNull(ui.address)
    }
}
