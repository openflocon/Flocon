package io.github.openflocon.flocondesktop.features.timeline.mapper

import kotlin.test.Test
import kotlin.test.assertEquals

class SplitUrlTest {

    @Test
    fun `an url is split in its host and its path with the query`() {
        assertEquals("api.acme.io" to "/v1/users?id=2", splitUrl("https://api.acme.io/v1/users?id=2"))
    }

    @Test
    fun `an url without path has the root path`() {
        assertEquals("api.acme.io" to "/", splitUrl("https://api.acme.io"))
        assertEquals("api.acme.io:8080" to "/", splitUrl("http://api.acme.io:8080/"))
    }

    @Test
    fun `a web socket url is split too`() {
        assertEquals("rt.acme.io" to "/socket", splitUrl("wss://rt.acme.io/socket"))
    }

    @Test
    fun `something that is not an url is kept as the path`() {
        assertEquals(null to "com.acme.Greeter/SayHello", splitUrl("com.acme.Greeter/SayHello"))
    }
}
