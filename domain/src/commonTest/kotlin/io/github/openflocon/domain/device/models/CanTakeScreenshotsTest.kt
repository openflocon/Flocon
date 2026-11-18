package io.github.openflocon.domain.device.models

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CanTakeScreenshotsTest {

    private fun device(platform: String, type: DeviceConnectionType) = DeviceDomainModel(
        deviceId = "id",
        deviceName = "name",
        platform = platform,
        connectionType = type,
    )

    @Test
    fun `an android over wifi cannot, it has no adb link`() {
        assertFalse(device("android", DeviceConnectionType.Wifi).canTakeScreenshots())
    }

    @Test
    fun `an android with the default connection can`() {
        assertTrue(device("android", DeviceConnectionType.Default).canTakeScreenshots())
    }

    @Test
    fun `an ios can whatever its connection, it does not use adb`() {
        assertTrue(device("ios", DeviceConnectionType.Wifi).canTakeScreenshots())
        assertTrue(device("ios", DeviceConnectionType.Default).canTakeScreenshots())
    }
}
