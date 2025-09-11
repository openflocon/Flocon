package io.github.openflocon.library.designsystem.components.escape

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

@Composable
fun EscapeHandler(
    enabled: Boolean = true,
    onEscape: () -> Boolean
) {
    val stack = LocalEscapeHandlerStack.current
    val onEscapeCallback by rememberUpdatedState(onEscape)

    DisposableEffect(enabled) {
        val handler = { onEscapeCallback() }

        if (enabled) {
            stack.add(handler)
        }
        onDispose {
            if (enabled) {
                stack.remove(handler)
            }
        }
    }
}
