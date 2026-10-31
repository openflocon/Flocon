package io.github.openflocon.flocon.error

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin

/**
 * Used by the `Flocon.xxxPlugin` accessors when the plugin is not installed:
 * logs it and returns [plugin], a no-op version, instead of crashing the app.
 */
fun <T : FloconPlugin> pluginNotInitialized(pluginName: String, plugin: T): T {
    FloconLogger.log("$pluginName is not initialized")
    return plugin
}