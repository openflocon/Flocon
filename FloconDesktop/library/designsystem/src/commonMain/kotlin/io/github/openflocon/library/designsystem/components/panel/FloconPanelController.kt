package io.github.openflocon.library.designsystem.components.panel

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

typealias PanelContent = (@Composable BoxScope.() -> Unit)

@Immutable
class FloconPanelController {
    private val _content = mutableStateOf<PanelContent?>(null)
    val content: State<PanelContent?> = _content

    fun display(content: PanelContent) {
        _content.value = content
    }

    fun hide() {
        _content.value = null
    }
}

@Composable
fun rememberFloconPanelController() = remember { FloconPanelController() }

val LocalFloconPanelController = staticCompositionLocalOf<FloconPanelController> {
    error("FloconPanelController not provided via CompositionLocalProvider")
}
