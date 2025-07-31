package io.github.openflocon.flocondesktop.common.ui

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun ContextualView(
    items: List<ContextualItem>,
    onSelect: (ContextualItem) -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    if (items.isNotEmpty()) {
        ContextMenuArea(
            items = {
                items.map {
                    ContextMenuItem(
                        label = it.text,
                        onClick = {
                            onSelect(it)
                        },
                    )
                }
            },
            content = content,
        )
    } else {
        content()
    }
}
