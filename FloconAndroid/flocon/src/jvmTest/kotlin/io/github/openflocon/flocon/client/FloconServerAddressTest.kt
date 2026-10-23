package io.github.openflocon.flocon.client

import io.github.openflocon.flocon.FloconConnectionMode
import kotlin.test.Test
import kotlin.test.assertEquals

class FloconServerAddressTest {

    @Test
    fun `adb mode uses the platform host and the default ports`() {
        val address = FloconConnectionMode.Adb.resolve(adbHost = { "10.0.2.2" })

        assertEquals(FloconServerAddress("10.0.2.2", 9023, 9024), address)
    }

    @Test
    fun `url mode uses the given host and the default ports`() {
        val address = FloconConnectionMode.Url(host = "192.168.1.20")
            .resolve(adbHost = { error("adb host must not be resolved in url mode") })

        assertEquals(FloconServerAddress("192.168.1.20", 9023, 9024), address)
    }

    @Test
    fun `bluetooth mode does not resolve the platform host`() {
        val address = FloconConnectionMode.Bluetooth
            .resolve(adbHost = { error("adb host must not be resolved in bluetooth mode") })

        assertEquals(FloconServerAddress("bluetooth", 0, 0), address)
    }

    @Test
    fun `url mode uses custom ports`() {
        val address = FloconConnectionMode.Url(host = "desktop.example.com", websocketPort = 1000, httpPort = 2000)
            .resolve(adbHost = { "localhost" })

        assertEquals(FloconServerAddress("desktop.example.com", 1000, 2000), address)
    }
}
