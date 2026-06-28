package io.github.openflocon.flocon.model

import io.github.openflocon.flocon.dsl.FloconMarker

@FloconMarker
data class FloconFileInfo(
    val path: String,
    val requestId: String,
)