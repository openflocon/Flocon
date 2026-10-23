package io.github.openflocon.flocondesktop.app.ui.delegates

import io.github.openflocon.domain.device.models.DeviceConnectionTypeDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel.ConnectionType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MapperTest {

    private val device = DeviceDomainModel(deviceId = "id", deviceName = "Pixel", platform = "android")

    private fun DeviceDomainModel.toUi(
        activeAddresses: Map<String, String> = emptyMap(),
        connectionTypes: Map<String, DeviceConnectionTypeDomainModel> = emptyMap(),
    ) = mapToUi(
        activeDevices = emptySet(),
        activeAddresses = activeAddresses,
        connectionTypes = connectionTypes,
        currentDeviceCapabilities = null,
    )

    @Test
    fun `a lan address is exposed to the ui`() {
        val ui = device.toUi(activeAddresses = mapOf("id" to "192.168.1.42"))

        assertEquals("192.168.1.42", ui.address)
    }

    @Test
    fun `an adb device has no address`() {
        val ui = device.toUi(activeAddresses = mapOf("id" to "127.0.0.1"))

        assertNull(ui.address)
    }

    @Test
    fun `a device never seen connecting is in the adb tab`() {
        assertEquals(ConnectionType.Adb, device.toUi().connectionType)
    }

    @Test
    fun `a network device is in the network tab`() {
        val ui = device.toUi(connectionTypes = mapOf("id" to DeviceConnectionTypeDomainModel.Network))

        assertEquals(ConnectionType.Network, ui.connectionType)
    }

    @Test
    fun `a tab only lists its devices`() {
        val other = DeviceDomainModel(deviceId = "other", deviceName = "iPhone", platform = "ios")
        val devices = mapListToUi(
            devices = listOf(device, other),
            activeDevices = emptySet(),
            activeAddresses = emptyMap(),
            connectionTypes = mapOf("other" to DeviceConnectionTypeDomainModel.Network),
        )

        assertEquals(listOf("id"), devices.inTab(ConnectionType.Adb).map { it.id })
        assertEquals(listOf("other"), devices.inTab(ConnectionType.Network).map { it.id })
    }
}
