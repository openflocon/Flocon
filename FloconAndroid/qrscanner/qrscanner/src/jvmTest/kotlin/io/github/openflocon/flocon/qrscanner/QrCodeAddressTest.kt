package io.github.openflocon.flocon.qrscanner

import kotlin.test.Test
import kotlin.test.assertEquals

class QrCodeAddressTest {
    @Test
    fun `a plain address is kept`() {
        assertEquals("192.168.1.20", addressFromQrCode("192.168.1.20"))
    }

    @Test
    fun `whitespace around the address is removed`() {
        assertEquals("my-mac.local", addressFromQrCode(" my-mac.local\n"))
    }

    @Test
    fun `a url gives its host`() {
        assertEquals("192.168.1.20", addressFromQrCode("flocon://192.168.1.20"))
        assertEquals("my-mac.local", addressFromQrCode("wss://my-mac.local:9023/path?token=1"))
        assertEquals("192.168.1.20", addressFromQrCode("https://user@192.168.1.20:9023#top"))
    }

    @Test
    fun `text that is not an address is kept so it is reported as invalid`() {
        assertEquals("hello world", addressFromQrCode("hello world"))
        assertEquals("://", addressFromQrCode("://"))
        assertEquals("flocon://", addressFromQrCode("flocon://"))
    }
}
