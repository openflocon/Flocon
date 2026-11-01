package io.github.openflocon.flocondesktop.features.crashreporter

import io.github.openflocon.flocondesktop.features.crashreporter.model.StackLine
import io.github.openflocon.flocondesktop.features.crashreporter.model.parseStackTrace
import io.github.openflocon.flocondesktop.features.crashreporter.model.symbolAndLocation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StackTraceTest {
    private val trace = """
        java.lang.IllegalStateException: my custom crash
         at io.github.app.Sample${'$'}11.invokeSuspend(Sample.kt:155)
         at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:34)
         at android.os.Looper.loop(Looper.java:392)
         Suppressed: kotlinx.coroutines.DiagnosticException: [a,
        b]
    """.trimIndent()

    @Test
    fun `classifies header, app, framework and suppressed lines`() {
        val parsed = parseStackTrace(trace)

        assertEquals(
            listOf(
                StackLine.Kind.Header,
                StackLine.Kind.App,
                StackLine.Kind.Framework,
                StackLine.Kind.Framework,
                StackLine.Kind.Suppressed,
                StackLine.Kind.Suppressed,
            ),
            parsed.lines.map { it.kind },
        )
        assertEquals(2, parsed.frameworkFramesCount)
        assertEquals(2, parsed.suppressed.size)
    }

    @Test
    fun `first app frame is the crash origin`() {
        val frame = parseStackTrace(trace).firstAppFrame

        assertEquals("Sample\$11.invokeSuspend" to "Sample.kt:155", frame?.symbolAndLocation())
    }

    @Test
    fun `no app frame when everything is framework`() {
        assertNull(parseStackTrace("java.lang.Error\n at android.os.Looper.loop(Looper.java:1)").firstAppFrame)
    }
}
