package io.github.openflocon.flocon.featureflags

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconRuntime
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame

class FloconFeatureFlagsAccessorTest {

    private object NoSender : FloconMessageSender {
        override fun send(plugin: String, method: String, body: String) = Unit
        override fun sendPendingMessages() = Unit
    }

    private val installed = FloconFeatureFlagsPluginImpl(NoSender, FloconEncoder(), Dispatchers.Unconfined)

    @AfterTest
    fun tearDown() {
        FloconRuntime.overrideEnabledForTests(null)
        FloconFeatureFlagsPluginImpl.plugin = null
    }

    @Test
    fun `the accessor returns the installed plugin`() {
        FloconFeatureFlagsPluginImpl.plugin = installed

        assertSame(installed, Flocon.featureFlagsPlugin)
    }

    @Test
    fun `the accessor returns a no-op when Flocon is turned off, even if installed`() {
        FloconFeatureFlagsPluginImpl.plugin = installed
        FloconRuntime.overrideEnabledForTests(false)

        assertFalse(Flocon.featureFlagsPlugin is FloconFeatureFlagsPluginImpl)
    }
}
