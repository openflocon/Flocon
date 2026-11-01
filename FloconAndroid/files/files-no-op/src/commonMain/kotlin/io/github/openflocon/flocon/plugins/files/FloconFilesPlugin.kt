package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.plugins.files.model.FloconFolderModel

class FloconFilesConfig : FloconPluginConfig {
    val roots = mutableListOf<String>()

    fun folder(displayName: String, absolutePath: String) {
        // no op
    }
}

interface FloconFilesPlugin : FloconPlugin {
    fun register(folder: FloconFolderModel)

    fun register(displayName: String, absolutePath: String) {
        register(FloconFolderModel(displayName = displayName, absolutePath = absolutePath))
    }
}

object FloconFiles : FloconPluginFactory<FloconFilesConfig, FloconFilesPlugin> {
    override val name: String = "Files"
    override val pluginId: String = "files"
    override fun createConfig(context: FloconContext) = FloconFilesConfig()
    override fun install(
        pluginConfig: FloconFilesConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconFilesPlugin {
        return FloconFilesPluginNoOpImpl
    }
}

internal object FloconFilesPluginNoOpImpl : FloconPlugin, FloconFilesPlugin {
    override val key: String = "files"

    override fun register(folder: FloconFolderModel) {
        // no op
    }

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

val Flocon.Companion.filesPlugin: FloconFilesPlugin
    get() = FloconFilesPluginNoOpImpl
