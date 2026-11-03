package io.github.openflocon.flocon.client

import io.github.openflocon.flocon.FloconContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.test.Test
import kotlin.test.assertEquals

class FloconClientPortsTest {

    private fun newClient() = FloconClient(
        context = FloconContext(appName = "test", packageName = "test"),
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
    )

    @Test
    fun `the default link uses the default websocket port`() {
        val client = newClient()
        assertEquals(9023, client.defaultLink.server.port)
        assertEquals(9024, client.httpPort)
    }

    @Test
    fun `the default link uses the configured websocket port`() {
        val client = newClient()
        client.setupPorts(websocketPort = 9100, httpPort = 9101)
        assertEquals(9100, client.defaultLink.server.port)
        assertEquals(9100, client.websocketPort)
        assertEquals(9101, client.httpPort)
    }
}
