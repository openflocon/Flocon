package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder

class FloconFilesConfig : FloconPluginConfig {
    val roots = mutableListOf<String>()
}

interface FloconFilesPlugin : FloconPlugin

object FloconFiles : FloconPluginFactory<FloconFilesConfig, FloconFilesPlugin> {
    override val name: String = "Files"
    override val pluginId: String = Protocol.ToDevice.Files.Plugin
    override fun createConfig(context: FloconContext) = FloconFilesConfig()
    override fun install(
        pluginConfig: FloconFilesConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconFilesPlugin {
        return FloconFilesPluginNoOpImpl()
    }
}

internal class FloconFilesPluginNoOpImpl : FloconPlugin, FloconFilesPlugin {
    override val key: String = "FILES"

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        // no op
    }

    override suspend fun onConnectedToServer() {
        // no op
    }
}
