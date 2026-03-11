package io.github.openflocon.flocon.pluginsold.crashreporter

import io.github.openflocon.flocon.FloconPlugin

class FloconCrashReporterConfig {
    var catchFatalErrors: Boolean = true
}

/**
 * Flocon Crash Reporter Plugin.
 */
//expect object FloconCrashReporter : FloconPluginFactory<FloconCrashReporterConfig, FloconCrashReporterPlugin>
//
interface FloconCrashReporterPlugin : FloconPlugin {
    fun setupCrashHandler()
}