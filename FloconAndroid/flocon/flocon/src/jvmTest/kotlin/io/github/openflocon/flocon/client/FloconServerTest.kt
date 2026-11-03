package io.github.openflocon.flocon.client

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FloconServerTest {

    @Test
    fun `reads an ip address or a host, the port is 9023 by default`() {
        assertEquals(FloconServer("192.168.1.20", 9023), parseFloconServer("192.168.1.20"))
        assertEquals(FloconServer("my-mac.local", 9023), parseFloconServer(" my-mac.local "))
        assertEquals(FloconServer("localhost", 9023), parseFloconServer("localhost"))
    }

    @Test
    fun `uses the default port given`() {
        assertEquals(FloconServer("localhost", 9500), parseFloconServer("localhost", defaultPort = 9500))
    }

    @Test
    fun `a port in the address wins over the default port`() {
        assertEquals(FloconServer("host", 9100), parseFloconServer("host:9100"))
        assertEquals(FloconServer("host", 9100), parseFloconServer("host:9100", defaultPort = 9500))
        assertEquals(FloconServer("192.168.1.20", 1), parseFloconServer("192.168.1.20:1"))
        assertEquals(FloconServer("host", 65535), parseFloconServer(" host:65535 "))
    }

    @Test
    fun `refuses an invalid port, a scheme, a path or an empty address`() {
        listOf(
            "", "  ", "host:0", "host:99999", "host:65536", "host:abc", "host:", "host:-1", "host:+80",
            ":9023", "a:b:c", "ws://host", "ws://host:9023", "host/path", "host:9023/path", "host?x=1", "my mac",
            "host: 9023",
        ).forEach { address ->
            assertFailsWith<IllegalArgumentException>("'$address' must be refused") { parseFloconServer(address) }
        }
    }
}
