package io.github.openflocon.flocon.crashreporter

import io.github.openflocon.flocon.Protocol
import kotlin.test.Test
import kotlin.test.assertEquals

class FloconCrashReporterPluginTest {

    @Test
    fun `has its own plugin id`() {
        // plugins are installed by id: sharing one with another plugin replaces it
        assertEquals(Protocol.FromDevice.CrashReporter.Plugin, FloconCrashReporter.pluginId)
    }
}
