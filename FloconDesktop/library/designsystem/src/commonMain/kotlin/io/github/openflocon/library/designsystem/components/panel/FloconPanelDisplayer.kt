package io.github.openflocon.library.designsystem.components.panel

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun FloconPanelDisplayer(
    handler: FloconPanelController,
    modifier: Modifier = Modifier,
) {
    val content by handler.content
    Box(modifier, contentAlignment = Alignment.TopEnd) {
        content?.invoke(this)
    }
}
