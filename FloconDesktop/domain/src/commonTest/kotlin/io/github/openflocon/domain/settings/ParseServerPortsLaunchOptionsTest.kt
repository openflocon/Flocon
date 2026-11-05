package io.github.openflocon.domain.settings

import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ServerPortsOverrides
import io.github.openflocon.domain.settings.usecase.ParseServerPortsLaunchOptions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ParseServerPortsLaunchOptionsTest {

    private fun parse(args: List<String> = emptyList(), env: Map<String, String> = emptyMap()) = ParseServerPortsLaunchOptions.parse(args, env)

    @Test
    fun `nothing given gives no override`() {
        assertEquals(Success(ServerPortsOverrides.None), parse())
    }

    @Test
    fun `equals form`() {
        assertEquals(
            Success(ServerPortsOverrides(websocketPort = 9100, httpPort = 9101)),
            parse(listOf("--websocket-port=9100", "--http-port=9101")),
        )
    }

    @Test
    fun `space form`() {
        assertEquals(
            Success(ServerPortsOverrides(websocketPort = 9100, httpPort = 9101)),
            parse(listOf("--websocket-port", "9100", "--http-port", "9101")),
        )
    }

    @Test
    fun `env var is used when there is no argument`() {
        assertEquals(
            Success(ServerPortsOverrides(websocketPort = 9200, httpPort = 9201)),
            parse(env = mapOf("FLOCON_WEBSOCKET_PORT" to "9200", "FLOCON_HTTP_PORT" to "9201")),
        )
    }

    @Test
    fun `argument wins over env var, per port`() {
        assertEquals(
            Success(ServerPortsOverrides(websocketPort = 9100, httpPort = 9201)),
            parse(
                args = listOf("--websocket-port=9100"),
                env = mapOf("FLOCON_WEBSOCKET_PORT" to "9200", "FLOCON_HTTP_PORT" to "9201"),
            ),
        )
    }

    @Test
    fun `a single port can be overridden`() {
        assertEquals(
            Success(ServerPortsOverrides(websocketPort = null, httpPort = 9101)),
            parse(listOf("--http-port=9101")),
        )
    }

    @Test
    fun `unknown arguments are ignored`() {
        assertEquals(
            Success(ServerPortsOverrides(websocketPort = 9100)),
            parse(listOf("--other", "--foo=bar", "--websocket-port=9100", "positional")),
        )
    }

    @Test
    fun `non numeric argument is an error`() {
        assertIs<Failure<String>>(parse(listOf("--websocket-port=abc")))
        assertIs<Failure<String>>(parse(listOf("--http-port")))
        assertIs<Failure<String>>(parse(listOf("--http-port=")))
    }

    @Test
    fun `non numeric env var is an error`() {
        val result = parse(env = mapOf("FLOCON_HTTP_PORT" to "nope"))
        assertIs<Failure<String>>(result)
        assertTrue("FLOCON_HTTP_PORT" in result.value)
    }

    @Test
    fun `an invalid argument is an error even when the env var is valid`() {
        assertIs<Failure<String>>(parse(listOf("--http-port=x"), mapOf("FLOCON_HTTP_PORT" to "9101")))
    }

    @Test
    fun `effective pair merges the override with the saved ports`() {
        val result = ParseServerPortsLaunchOptions.validateEffective(
            overrides = ServerPortsOverrides(websocketPort = 9100),
            saved = ServerPortsDomainModel(websocketPort = 9300, httpPort = 9301),
        )
        assertEquals(Success(ServerPortsDomainModel(websocketPort = 9100, httpPort = 9301)), result)
    }

    @Test
    fun `effective pair is invalid when the override collides with the saved port`() {
        val result = ParseServerPortsLaunchOptions.validateEffective(
            overrides = ServerPortsOverrides(websocketPort = 9301),
            saved = ServerPortsDomainModel(websocketPort = 9300, httpPort = 9301),
        )
        assertIs<Failure<String>>(result)
    }

    @Test
    fun `effective pair reuses the settings validation`() {
        listOf(
            ServerPortsOverrides(websocketPort = 80), // out of range
            ServerPortsOverrides(httpPort = 9042), // mcp
            ServerPortsOverrides(websocketPort = 9024), // reserved default
        ).forEach {
            assertIs<Failure<String>>(ParseServerPortsLaunchOptions.validateEffective(it, ServerPortsDomainModel()))
        }
        assertIs<Success<*>>(
            ParseServerPortsLaunchOptions.validateEffective(ServerPortsOverrides.None, ServerPortsDomainModel()),
        )
    }
}
