package io.github.openflocon.flocondesktop.features.proxy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProxyEventTest {

    @Test
    fun `parses the ready event`() {
        val event = parseProxyEvent("""{"event":"ready","proxyPort":8080,"caPort":8081,"ips":["192.168.1.12"]}""")
        assertEquals(ProxyEvent.Ready(8080, 8081, listOf("192.168.1.12")), event)
    }

    @Test
    fun `ready without ips gives an empty list`() {
        val event = parseProxyEvent("""{"event":"ready","proxyPort":8080,"caPort":8081}""")
        assertEquals(ProxyEvent.Ready(8080, 8081, emptyList()), event)
    }

    @Test
    fun `ready without port is ignored`() {
        assertNull(parseProxyEvent("""{"event":"ready","caPort":8081}"""))
    }

    @Test
    fun `parses error and tls-error events`() {
        assertEquals(ProxyEvent.Error("port in use"), parseProxyEvent("""{"event":"error","message":"port in use"}"""))
        assertEquals(
            ProxyEvent.TlsError("api.example.com", "cert-rejected"),
            parseProxyEvent("""{"event":"tls-error","host":"api.example.com","reason":"cert-rejected"}"""),
        )
    }

    @Test
    fun `ignores unknown events and non json lines`() {
        assertNull(parseProxyEvent("""{"event":"something"}"""))
        assertNull(parseProxyEvent("not json"))
        assertNull(parseProxyEvent(""))
    }

    @Test
    fun `platform folder matches the appResources layout`() {
        assertEquals("macos-arm64", currentPlatformFolder("Mac OS X", "aarch64"))
        assertEquals("macos-x64", currentPlatformFolder("Mac OS X", "x86_64"))
        assertEquals("windows-x64", currentPlatformFolder("Windows 11", "amd64"))
        assertEquals("linux-x64", currentPlatformFolder("Linux", "amd64"))
        assertEquals("linux-arm64", currentPlatformFolder("Linux", "aarch64"))
    }
}
