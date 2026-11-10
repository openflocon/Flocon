package io.github.openflocon.flocon.client

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FloconServerTest {

    @Test
    fun `reads an ip address or a host, the ports are 9023 and 9024 by default`() {
        assertEquals(FloconServer("192.168.1.20", 9023, 9024), parseFloconServer("192.168.1.20"))
        assertEquals(FloconServer("my-mac.local", 9023, 9024), parseFloconServer(" my-mac.local "))
        assertEquals(FloconServer("localhost", 9023, 9024), parseFloconServer("localhost"))
    }

    @Test
    fun `uses the default ports given`() {
        assertEquals(
            FloconServer("localhost", 9500, 9501),
            parseFloconServer("localhost", defaultPort = 9500, defaultHttpPort = 9501),
        )
    }

    @Test
    fun `a websocket port in the address wins, the http port stays the default`() {
        assertEquals(FloconServer("host", 9100, 9024), parseFloconServer("host:9100"))
        assertEquals(FloconServer("host", 10023, 9501), parseFloconServer("host:10023", 9500, 9501))
        assertEquals(FloconServer("192.168.1.20", 1, 9024), parseFloconServer("192.168.1.20:1"))
        assertEquals(FloconServer("host", 65535, 9024), parseFloconServer(" host:65535 "))
    }

    @Test
    fun `reads both ports`() {
        assertEquals(FloconServer("192.168.1.10", 10023, 10024), parseFloconServer("192.168.1.10:10023:10024"))
        assertEquals(FloconServer("host", 10023, 10024), parseFloconServer("host:10023:10024", 9500, 9501))
        assertEquals(FloconServer("host", 1, 65535), parseFloconServer("host:1:65535"))
    }

    @Test
    fun `refuses an invalid port, a scheme, a path or an empty address`() {
        listOf(
            "", "  ", "host:0", "host:99999", "host:65536", "host:abc", "host:", "host:-1", "host:+80",
            ":9023", ":1:2", "a:b:c", "host:a:b", "host:1:2:3", "host:1:", "host::2", "host:0:9", "host:9:0",
            "host:9:99999", "host:9:65536", "host:123456:9", "host:9:123456", "host:1:b", "ws://host",
            "ws://host:9023", "host/path", "host:9023/path", "host?x=1", "my mac", "host: 9023", "host:9: 2",
        ).forEach { address ->
            assertFailsWith<IllegalArgumentException>("'$address' must be refused") { parseFloconServer(address) }
        }
    }
}
