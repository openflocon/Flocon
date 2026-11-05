package io.github.openflocon.flocon.client

import io.github.openflocon.flocon.FloconContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class FloconClientPortsTest {

    private fun newClient() = FloconClient(
        context = FloconContext(appName = "test", packageName = "test"),
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
    )

    @Test
    fun `the default link uses the default ports`() {
        val client = newClient()
        assertEquals(9023, client.defaultLink.server.port)
        assertEquals(9024, client.defaultLink.server.httpPort)
        assertEquals(9024, client.httpPort)
    }

    @Test
    fun `the default link uses the configured ports`() {
        val client = newClient()
        client.setupPorts(websocketPort = 9100, httpPort = 9101)
        assertEquals(9100, client.defaultLink.server.port)
        assertEquals(9101, client.defaultLink.server.httpPort)
        assertEquals(9100, client.websocketPort)
        assertEquals(9101, client.httpPort)
    }

    @Test
    fun `a file goes through the default link, with its http port, when no other link is connected`() {
        val client = newClient()
        client.setupPorts(websocketPort = 9100, httpPort = 9101)
        client.addExtraLink(FloconServer("192.168.1.10", 10023, 10024)) // not connected yet
        assertSame(client.defaultLink, client.uploadLink)
        assertEquals(9101, client.uploadLink.server.httpPort)
    }

    @Test
    fun `a file uses the http port of the connected extra link`() {
        val client = newClient()
        client.setupPorts(websocketPort = 9100, httpPort = 9101)
        val extra = client.addExtraLink(FloconServer("192.168.1.10", 10023, 10024))
        extra.isConnected = true
        assertSame(extra, client.uploadLink)
        assertEquals("192.168.1.10", client.uploadLink.server.host)
        assertEquals(10024, client.uploadLink.server.httpPort)
    }
}
