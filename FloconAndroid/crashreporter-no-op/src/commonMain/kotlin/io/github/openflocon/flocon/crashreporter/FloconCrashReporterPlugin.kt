package io.github.openflocon.flocon.crashreporter

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory

class FloconCrashReporterConfig : FloconPluginConfig {
    var catchFatalErrors: Boolean = true
}

interface FloconCrashReporterPlugin : FloconPlugin {
    fun setupCrashHandler()
}

object FloconCrashReporter : FloconPluginFactory<FloconCrashReporterConfig, FloconCrashReporterPlugin> {
    override val name: String = "CrashReporter"
    override val pluginId: String = "crashreporter"

    override fun createConfig(context: FloconContext) = FloconCrashReporterConfig()

    override fun install(
        pluginConfig: FloconCrashReporterConfig,
        floconConfig: FloconConfig,
        encoder: io.github.openflocon.flocon.core.FloconEncoder
    ): FloconCrashReporterPlugin {
        return FloconCrashReporterNoOpImpl
    }
}

internal object FloconCrashReporterNoOpImpl : FloconPlugin, FloconCrashReporterPlugin {
    override val key: String = "crashreporter"

    override fun setupCrashHandler() {
        // no op
    }

    override suspend fun onConnectedToServer() {
        // no op
    }

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        // no op
    }
}

val Flocon.Companion.crashReporterPlugin: FloconCrashReporterPlugin
    get() = FloconCrashReporterNoOpImpl
