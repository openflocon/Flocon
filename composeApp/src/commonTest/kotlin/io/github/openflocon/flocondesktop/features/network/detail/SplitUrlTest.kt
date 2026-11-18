package io.github.openflocon.flocondesktop.features.network.detail

import io.github.openflocon.flocondesktop.features.network.detail.mapper.splitUrl
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailHeaderUi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SplitUrlTest {

    @Test
    fun `splits host path and query params`() {
        val parts = splitUrl("https://api.example.com/v1/orders?include=items&dryRun=false#top")

        assertEquals("api.example.com", parts.host)
        assertEquals("/v1/orders", parts.path)
        assertEquals(
            listOf(
                NetworkDetailHeaderUi(name = "include", value = "items"),
                NetworkDetailHeaderUi(name = "dryRun", value = "false"),
            ),
            parts.queryParams,
        )
    }

    @Test
    fun `host only url has a root path`() {
        val parts = splitUrl("https://example.com")

        assertEquals("example.com", parts.host)
        assertEquals("/", parts.path)
        assertTrue(parts.queryParams.isEmpty())
    }

    @Test
    fun `query param without value`() {
        val parts = splitUrl("http://localhost:8080/ping?debug")

        assertEquals("localhost:8080", parts.host)
        assertEquals(listOf(NetworkDetailHeaderUi(name = "debug", value = "")), parts.queryParams)
    }

    @Test
    fun `input without scheme is the path`() {
        val parts = splitUrl("com.example.Greeter/SayHello")

        assertEquals("", parts.host)
        assertEquals("com.example.Greeter/SayHello", parts.path)
    }

    @Test
    fun `scheme separator inside the query is not a scheme`() {
        val parts = splitUrl("com.example.Greeter/SayHello?url=http://x")

        assertEquals("", parts.host)
        assertEquals("com.example.Greeter/SayHello", parts.path)
        assertEquals(listOf(NetworkDetailHeaderUi(name = "url", value = "http://x")), parts.queryParams)
    }

    @Test
    fun `empty and nameless query params are ignored`() {
        val parts = splitUrl("https://example.com/a?&&=1&b=2")

        assertEquals(listOf(NetworkDetailHeaderUi(name = "b", value = "2")), parts.queryParams)
    }
}
