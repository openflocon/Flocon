package io.github.openflocon.flocon.error

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.dsl.FloconMarker

// Maybe remove it, and make plugins nullable to avoid app crashing
@FloconMarker
fun pluginNotInitialized(pluginName: String): Nothing = error("$pluginName is not initialized")

@FloconMarker
fun <T : FloconPlugin> pluginNotInitialized(pluginName: String, plugin: T): T {
    FloconLogger.log("$pluginName is not initialized")
    return plugin
}