package io.github.openflocon.flocon.plugins.crashreporter

import io.github.openflocon.flocon.*

class FloconCrashReporterConfig {
    var catchFatalErrors: Boolean = true
}

/**
 * Flocon Crash Reporter Plugin.
 */
expect object FloconCrashReporter : FloconPluginFactory<FloconCrashReporterConfig, FloconCrashReporterPlugin>

interface FloconCrashReporterPlugin : FloconPlugin {
    fun setupCrashHandler()
}