package io.github.openflocon.library.designsystem.components.escape

import androidx.compose.runtime.staticCompositionLocalOf

val LocalEscapeHandlerStack = staticCompositionLocalOf<MutableList<() -> Boolean>> {
    error("No EscapeHandlerProvider found!")
}
