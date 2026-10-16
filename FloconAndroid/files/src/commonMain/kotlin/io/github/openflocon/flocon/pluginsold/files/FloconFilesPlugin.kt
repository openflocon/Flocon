package io.github.openflocon.flocon.pluginsold.files

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.core.FloconEncoder

class FloconFilesConfig : FloconPluginConfig {
    val roots = mutableListOf<String>()
}

/**
 * Flocon Files Plugin.
 * Used to inspect and download files from the device.
 */
object FloconFiles : FloconPluginFactory<FloconFilesConfig, FloconFilesPlugin> {
    override fun createConfig(context: FloconContext): FloconFilesConfig {
        TODO("Not yet implemented")
    }

    override fun install(
        pluginConfig: FloconFilesConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconFilesPlugin {
        TODO("Not yet implemented")
    }

    override val name: String
        get() = TODO("Not yet implemented")
    override val pluginId: String
        get() = TODO("Not yet implemented")
}

interface FloconFilesPlugin : FloconPlugin