package io.github.openflocon.domain.device.usecase

import kotlin.test.Test
import kotlin.test.assertEquals

class ConnectedDevicesTest {

    @Test
    fun `no device attached`() {
        assertEquals(0, countConnectedDevices("List of devices attached\n\n"))
    }

    @Test
    fun `a phone and an emulator are two devices`() {
        val output = "List of devices attached\nR5CY70X7WXX\tdevice\nemulator-5554\tdevice\n\n"

        assertEquals(2, countConnectedDevices(output))
    }

    @Test
    fun `a device that is offline or unauthorized is not counted`() {
        val output = "List of devices attached\nR5CY70X7WXX\tdevice\nemulator-5554\toffline\nABC\tunauthorized\n"

        assertEquals(1, countConnectedDevices(output))
    }

    @Test
    fun `windows line endings are handled`() {
        assertEquals(1, countConnectedDevices("List of devices attached\r\nR5CY70X7WXX\tdevice\r\n\r\n"))
    }

    @Test
    fun `an unknown adb serial is refused only when several devices could answer`() {
        assertEquals(true, isCaptureAmbiguous(serial = null, connectedDevices = 2))
        assertEquals(false, isCaptureAmbiguous(serial = null, connectedDevices = 1))
        assertEquals(false, isCaptureAmbiguous(serial = null, connectedDevices = 0))
        assertEquals(false, isCaptureAmbiguous(serial = "R5CY70X7WXX", connectedDevices = 2))
    }
}
