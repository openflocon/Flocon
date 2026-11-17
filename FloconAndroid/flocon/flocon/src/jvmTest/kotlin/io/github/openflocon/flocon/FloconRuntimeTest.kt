package io.github.openflocon.flocon

import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FloconRuntimeTest {

    @AfterTest
    fun tearDown() {
        FloconRuntime.overrideEnabledForTests(null)
    }

    @Test
    fun `Flocon is on when flocon-no-op is not in the app`() {
        assertTrue(FloconRuntime.isEnabled())
    }

    @Test
    fun `startFlocon starts nothing when Flocon is turned off`() {
        FloconRuntime.overrideEnabledForTests(false)
        var configured = false

        startFlocon(FloconContext(appName = "test", packageName = "test")) {
            configured = true
        }

        assertFalse(configured)
    }

    @Test
    fun `connect does nothing when Flocon is turned off`() = runTest {
        FloconRuntime.overrideEnabledForTests(false)

        assertEquals(FloconConnectResult.NotStarted, Flocon.connect("192.168.1.20"))
        Flocon.disconnect()
        assertEquals(FloconConnectionState.Idle, Flocon.connectionState.value)
    }

    @Test
    fun `floconPlugin returns the no-op when Flocon is turned off, even if installed`() {
        FloconRuntime.overrideEnabledForTests(false)

        val plugin = floconPlugin("Test", noOp = { TestPlugin("no-op") }, installed = { TestPlugin("installed") })

        assertEquals("no-op", plugin.key)
    }

    @Test
    fun `floconPlugin returns the installed plugin when Flocon is on`() {
        val plugin = floconPlugin("Test", noOp = { TestPlugin("no-op") }, installed = { TestPlugin("installed") })

        assertEquals("installed", plugin.key)
    }

    @Test
    fun `floconPlugin returns the no-op when the plugin is not installed`() {
        val plugin = floconPlugin("Test", noOp = { TestPlugin("no-op") }, installed = { null })

        assertEquals("no-op", plugin.key)
    }

    private class TestPlugin(override val key: String) : FloconPlugin {
        override suspend fun onMessageReceived(method: String, body: String) = Unit
        override suspend fun onConnectedToServer() = Unit
    }
}
