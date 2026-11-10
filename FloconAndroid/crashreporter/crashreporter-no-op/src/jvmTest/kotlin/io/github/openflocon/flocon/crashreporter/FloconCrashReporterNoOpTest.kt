package io.github.openflocon.flocon.crashreporter

import kotlin.test.Test

class FloconCrashReporterNoOpTest {

    @Test
    fun `setupCrashHandler does nothing`() {
        // must not replace the app uncaught exception handler
        val handler = Thread.getDefaultUncaughtExceptionHandler()

        FloconCrashReporterNoOpImpl.setupCrashHandler()

        kotlin.test.assertSame(handler, Thread.getDefaultUncaughtExceptionHandler())
    }
}
