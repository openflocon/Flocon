package io.github.openflocon.library.designsystem.components.escape

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type

@Composable
fun EscapeHandlerProvider(content: @Composable () -> Unit) {
    val handlers = remember { mutableStateListOf<() -> Boolean>() }

    Box(
        modifier = Modifier.onPreviewKeyEvent { event ->
            if (event.key == Key.Escape && event.type == KeyEventType.KeyDown) {
                // on prend le dernier handler enregistré
                handlers.lastOrNull()?.invoke() ?: false
            } else {
                false
            }
        }
    ) {
        CompositionLocalProvider(LocalEscapeHandlerStack provides handlers) {
            content()
        }
    }
}
