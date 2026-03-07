package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.*

class FloconFilesConfig {
    val roots = mutableListOf<String>()
}

/**
 * Flocon Files Plugin.
 * Used to inspect and download files from the device.
 */
expect object FloconFiles : FloconPluginFactory<FloconFilesConfig, FloconFilesPlugin>

interface FloconFilesPlugin : FloconPlugin