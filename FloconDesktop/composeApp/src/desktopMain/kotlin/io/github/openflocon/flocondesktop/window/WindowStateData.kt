package io.github.openflocon.flocondesktop.window

import kotlinx.serialization.Serializable

@Serializable
data class WindowStateData(
    val width: Int,
    val height: Int,
    val x: Int,
    val y: Int,
)
