package io.github.openflocon.flocon.websocket

import io.github.openflocon.flocon.dsl.FloconMarker

@FloconMarker
internal actual fun buildFloconHttpClient(): FloconHttpClient = FloconHttpClientAndroid()