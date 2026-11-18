package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.device.models.CurrentDeviceAndAppDomainModel
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ScreenshotContextMapperTest {

    private val app = DeviceAppDomainModel(name = "Sample", packageName = "io.sample", iconEncoded = "abc", lastAppInstance = 1L, floconVersionOnDevice = "1.0.0")

    private fun context(platform: String, app: DeviceAppDomainModel?) = CurrentDeviceAndAppDomainModel(
        device = DeviceDomainModel(deviceId = "d", deviceName = "Pixel 8", platform = platform),
        app = app,
    ).toScreenshotContextUi()

    @Test
    fun `the platform is written for the user`() {
        assertEquals("Android", context("android", app).platform)
        assertEquals("iOS", context("ios", app).platform)
        assertEquals("custom", context("custom", app).platform)
    }

    @Test
    fun `the app and the device are kept`() {
        val ui = context("android", app)
        assertEquals("Sample", ui.appName)
        assertEquals("abc", ui.appIconEncoded)
        assertEquals("Pixel 8", ui.deviceName)
    }

    @Test
    fun `no app, no name`() {
        assertNull(context("android", null).appName)
        assertNull(context("android", app.copy(name = " ")).appName)
    }
}
