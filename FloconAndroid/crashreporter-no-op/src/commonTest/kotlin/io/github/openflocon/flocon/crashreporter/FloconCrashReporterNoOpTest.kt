package io.github.openflocon.flocon.crashreporter

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class FloconCrashReporterNoOpTest {

    private val plugin = FloconCrashReporterNoOpImpl()

    @Test
    fun `setupCrashHandler does nothing`() = runTest {
        plugin.setupCrashHandler()
        assertTrue(true)
    }
}
