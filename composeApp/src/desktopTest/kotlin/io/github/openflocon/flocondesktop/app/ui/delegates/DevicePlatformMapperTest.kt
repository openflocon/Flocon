package io.github.openflocon.flocondesktop.app.ui.delegates

import io.github.openflocon.domain.device.models.DeviceConnectionType
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DevicesStateUiModel
import kotlin.test.Test
import kotlin.test.assertEquals

class DevicePlatformMapperTest {

    private fun device(id: String, platform: String) = DeviceDomainModel(
        deviceId = id,
        deviceName = id,
        platform = platform,
        connectionType = DeviceConnectionType.Wifi,
    )

    @Test
    fun `proxy platform maps to its own platform`() {
        val ui = device("proxy-192.168.1.12", "proxy").mapToUi(activeDevices = emptySet(), currentDeviceCapabilities = null)
        assertEquals(DeviceItemUiModel.Platform.Proxy, ui.platform)
    }

    @Test
    fun `proxy devices get their own group after desktop`() {
        val devices = mapListToUi(
            devices = listOf(device("a", "android"), device("p", "proxy"), device("d", "desktop"), device("x", "other")),
            activeDevices = emptySet(),
        )
        val state = DevicesStateUiModel.WithDevices(devices = devices, deviceSelected = devices.first())
        assertEquals(
            listOf(
                DeviceItemUiModel.Platform.Android,
                DeviceItemUiModel.Platform.Desktop,
                DeviceItemUiModel.Platform.Proxy,
                DeviceItemUiModel.Platform.Unknown,
            ),
            state.groups.map { it.platform },
        )
    }
}
