package io.github.openflocon.flocon.error

import io.github.openflocon.flocon.dsl.FloconMarker

@FloconMarker
fun pluginNotInitialized(pluginName: String): Nothing = error("$pluginName is not initialized")