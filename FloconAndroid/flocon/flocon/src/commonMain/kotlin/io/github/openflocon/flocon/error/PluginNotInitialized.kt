package io.github.openflocon.flocon.error

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconRuntime
import io.github.openflocon.flocon.InternalFloconApi
import io.github.openflocon.flocon.FloconPlugin

/**
 * Used by the `Flocon.xxxPlugin` accessors when the plugin is not installed:
 * logs it and returns [plugin], a no-op version, instead of crashing the app.
 * Nothing is logged when Flocon is turned off by `flocon-no-op`.
 */
@OptIn(InternalFloconApi::class)
fun <T : FloconPlugin> pluginNotInitialized(pluginName: String, plugin: T): T {
    if (FloconRuntime.isEnabled()) {
        FloconLogger.log("$pluginName is not initialized")
    }
    return plugin
}