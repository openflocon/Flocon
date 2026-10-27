package io.github.openflocon.flocon.client

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FloconServerTest {

    @Test
    fun `reads a websocket address`() {
        assertEquals(FloconServer("192.168.1.20", 9023), parseFloconServer("ws://192.168.1.20:9023"))
        assertEquals(FloconServer("192.168.1.20", 9100), parseFloconServer("ws://192.168.1.20:9100/"))
    }

    @Test
    fun `reads an address without scheme or port`() {
        assertEquals(FloconServer("my-mac.local", 9023), parseFloconServer("my-mac.local"))
        assertEquals(FloconServer("my-mac.local", 9050), parseFloconServer(" my-mac.local:9050 "))
        assertEquals(FloconServer("localhost", 9023), parseFloconServer("http://localhost"))
    }

    @Test
    fun `refuses what it cannot read`() {
        assertFailsWith<IllegalArgumentException> { parseFloconServer("") }
        assertFailsWith<IllegalArgumentException> { parseFloconServer("wss://192.168.1.20:9023") }
        assertFailsWith<IllegalArgumentException> { parseFloconServer("ws://:9023") }
        assertFailsWith<IllegalArgumentException> { parseFloconServer("ws://host:notaport") }
        assertFailsWith<IllegalArgumentException> { parseFloconServer("ws://host:70000") }
    }
}
