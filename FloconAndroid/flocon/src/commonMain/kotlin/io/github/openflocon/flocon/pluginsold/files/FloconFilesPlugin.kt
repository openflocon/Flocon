package io.github.openflocon.flocon.pluginsold.files

import io.github.openflocon.flocon.*

class FloconFilesConfig {
    val roots = mutableListOf<String>()
}

/**
 * Flocon Files Plugin.
 * Used to inspect and download files from the device.
 */
object FloconFiles : FloconPluginFactory<FloconFilesConfig, FloconFilesPlugin> {
    override fun createConfig(): FloconFilesConfig {
        TODO("Not yet implemented")
    }

    override fun install(config: Any, app: FloconApp): FloconFilesPlugin {
        TODO("Not yet implemented")
    }

    override val name: String
        get() = TODO("Not yet implemented")
}

interface FloconFilesPlugin : FloconPlugin