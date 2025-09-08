package io.github.openflocon.flocondesktop.common.ui

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem

@Composable
actual fun ContextualView(
    items: List<FloconContextMenuItem>,
    modifier: Modifier,
    content: @Composable (() -> Unit)
) {
    if (items.isNotEmpty()) {
        ContextMenuArea(
            items = { items },
            content = content,
        )
    } else {
        content()
    }
}
