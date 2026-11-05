package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.device.models.DeviceConnectionType
import io.github.openflocon.domain.device.models.DeviceDomainModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ObserveCurrentDeviceCapabilitiesUseCaseTest {

    private fun capabilities(platform: String, connectionType: DeviceConnectionType) = computeCapabilities(
        device = DeviceDomainModel("id", "name", platform, connectionType),
    )

    @Test
    fun `android over usb has every capability`() {
        val result = assertNotNull(capabilities("android", DeviceConnectionType.Default))
        assertTrue(result.screenshot && result.recordScreen && result.deeplinks && result.files)
    }

    @Test
    fun `android over wifi loses the adb capabilities only`() {
        val result = assertNotNull(capabilities("android", DeviceConnectionType.Wifi))
        assertFalse(result.screenshot)
        assertFalse(result.recordScreen)
        assertFalse(result.deeplinks)
        assertTrue(result.files)
        assertTrue(result.sharedPreferences)
    }

    @Test
    fun `ios capabilities do not depend on the connection type`() {
        assertEquals(
            capabilities("ios", DeviceConnectionType.Default),
            capabilities("ios", DeviceConnectionType.Wifi),
        )
    }
}
