package io.github.openflocon.flocondesktop.features.proxy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProxyDeviceLinkerTest {

    @Test
    fun `only the ready devices of adb devices are kept`() {
        val output = """
            List of devices attached
            R5CT1234ABC	device
            emulator-5554	device
            192.168.1.20:5555	offline
            0A1B2C	unauthorized

        """.trimIndent()
        assertEquals(listOf("R5CT1234ABC", "emulator-5554"), parseAdbSerials(output))
    }

    @Test
    fun `the ipv4 addresses of the phone, without the loopback`() {
        val output = """
            1: lo    inet 127.0.0.1/8 scope host lo\       valid_lft forever preferred_lft forever
            30: wlan0    inet 192.168.1.12/24 brd 192.168.1.255 scope global wlan0\       valid_lft 86000sec
            31: rmnet_data0    inet 10.120.4.7/27 scope global rmnet_data0\       valid_lft forever
        """.trimIndent()
        assertEquals(listOf("192.168.1.12", "10.120.4.7"), parseIpv4s(output))
    }

    @Test
    fun `the ip of a serial of adb over wifi`() {
        assertEquals("192.168.1.20", ipOfAdbSerial("192.168.1.20:5555"))
        assertNull(ipOfAdbSerial("R5CT1234ABC"))
        assertNull(ipOfAdbSerial("adb-R5CT1234ABC-xyz._adb-tls-connect._tcp"))
    }

    @Test
    fun `the devices command links each phone by its ips and its own port`() {
        val sdkDevice = ProxyLinkedDevice("android-id-of-the-app", "Google Pixel 8", "android", connectionType = null)
        val adbDevice = ProxyLinkedDevice("android-R5CT", "samsung SM-S911B", "android", connectionType = "wifi")
        val command = buildProxyDevicesCommand(
            phones = listOf(
                ProxyPhone(serial = "pixel", ips = listOf("192.168.1.12"), device = sdkDevice),
                ProxyPhone(serial = "R5CT", ips = emptyList(), device = adbDevice),
                ProxyPhone(serial = "no-reverse", ips = listOf("192.168.1.30"), device = adbDevice),
            ),
            portBySerial = mapOf("pixel" to 18100, "R5CT" to 18101),
        )
        val pixel = """{"deviceId":"android-id-of-the-app","deviceName":"Google Pixel 8","platform":"android"}"""
        val samsung = """{"deviceId":"android-R5CT","deviceName":"samsung SM-S911B","platform":"android","connectionType":"wifi"}"""
        assertEquals(
            """{"cmd":"devices","ips":{"192.168.1.12":$pixel,"192.168.1.30":$samsung},"ports":{"18100":$pixel,"18101":$samsung}}""",
            command.toString(),
        )
    }
}
