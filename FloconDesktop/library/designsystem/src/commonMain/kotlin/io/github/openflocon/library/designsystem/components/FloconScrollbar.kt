package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FloconVerticalScrollbar(
    adapter: ScrollbarAdapter,
    modifier: Modifier = Modifier
) {
    VerticalScrollbar(
        adapter = adapter,
        modifier = modifier
    )
}
