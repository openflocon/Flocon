package io.github.openflocon.flocondesktop.common.ui

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import kotlinx.collections.immutable.ImmutableList

@Composable
actual fun ContextualView(
    items: ImmutableList<FloconContextMenuItem>,
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
