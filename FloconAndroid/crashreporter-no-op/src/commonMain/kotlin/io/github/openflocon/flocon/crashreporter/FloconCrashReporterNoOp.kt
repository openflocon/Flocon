package io.github.openflocon.flocon.crashreporter

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol

class FloconCrashReporterConfig : FloconPluginConfig {
    var catchFatalErrors: Boolean = true
}

interface FloconCrashReporterPlugin : FloconPlugin {
    fun setupCrashHandler()
}

object FloconCrashReporter : FloconPluginFactory<FloconCrashReporterConfig, FloconCrashReporterPlugin> {
    override val name: String = "CrashReporter"
    override val pluginId: String = Protocol.ToDevice.Analytics.Plugin // Same as real impl

    override fun createConfig(context: FloconContext) = FloconCrashReporterConfig()

    override fun install(
        pluginConfig: FloconCrashReporterConfig,
        floconConfig: FloconConfig,
        encoder: io.github.openflocon.flocon.core.FloconEncoder
    ): FloconCrashReporterPlugin {
        return FloconCrashReporterNoOpImpl()
    }
}

internal class FloconCrashReporterNoOpImpl : FloconPlugin, FloconCrashReporterPlugin {
    override val key: String = "CRASH_REPORTER"

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
