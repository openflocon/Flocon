package io.github.openflocon.flocon.client

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FloconServerTest {

    @Test
    fun `reads an ip address or a host, the port is always 9023`() {
        assertEquals(FloconServer("192.168.1.20", 9023), parseFloconServer("192.168.1.20"))
        assertEquals(FloconServer("my-mac.local", 9023), parseFloconServer(" my-mac.local "))
        assertEquals(FloconServer("localhost", 9023), parseFloconServer("localhost"))
    }

    @Test
    fun `refuses a port, a scheme, a path or an empty address`() {
        assertFailsWith<IllegalArgumentException> { parseFloconServer("") }
        assertFailsWith<IllegalArgumentException> { parseFloconServer("192.168.1.20:9023") }
        assertFailsWith<IllegalArgumentException> { parseFloconServer("192.168.1.20:9100") }
        assertFailsWith<IllegalArgumentException> { parseFloconServer("ws://192.168.1.20") }
        assertFailsWith<IllegalArgumentException> { parseFloconServer("192.168.1.20/path") }
        assertFailsWith<IllegalArgumentException> { parseFloconServer("my mac") }
    }
}
