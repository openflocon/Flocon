package io.github.openflocon.flocondesktop.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class ContextualItem(
    val id: String,
    val text: String,
)

@Composable
expect fun ContextualView( // right click on desktop
    items: List<ContextualItem>,
    onSelect: (ContextualItem) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
)
