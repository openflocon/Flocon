package io.github.openflocon.flocondesktop.common.ui

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun ContextualView(
    items: List<ContextualItem>,
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
                            it.onClick()
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
