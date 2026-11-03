package io.github.openflocon.domain.adb

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WirelessAdbTest {

    @Test
    fun `a serial with an address or a pairing service is wireless`() {
        assertTrue(isWirelessAdbSerial("192.168.1.20:5555"))
        assertTrue(isWirelessAdbSerial("adb-R5CY70X7WXX-AbCdEf._adb-tls-connect._tcp"))
        assertTrue(isWirelessAdbSerial("fe80::1:5555"))
    }

    @Test
    fun `a cable or an emulator is not wireless`() {
        assertFalse(isWirelessAdbSerial("R5CY70X7WXX"))
        assertFalse(isWirelessAdbSerial("emulator-5554"))
    }
}
