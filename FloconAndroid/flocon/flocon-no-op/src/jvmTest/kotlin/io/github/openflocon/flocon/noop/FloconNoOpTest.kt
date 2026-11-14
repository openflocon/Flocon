package io.github.openflocon.flocon.noop

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconRuntime
import io.github.openflocon.flocon.startFlocon
import kotlin.test.Test
import kotlin.test.assertFalse

class FloconNoOpTest {

    @Test
    fun `flocon-no-op turns Flocon off`() {
        assertFalse(FloconRuntime.isEnabled())
    }

    @Test
    fun `startFlocon starts nothing with flocon-no-op`() {
        var configured = false

        startFlocon(FloconContext(appName = "test", packageName = "test")) {
            configured = true
        }

        assertFalse(configured)
    }
}
