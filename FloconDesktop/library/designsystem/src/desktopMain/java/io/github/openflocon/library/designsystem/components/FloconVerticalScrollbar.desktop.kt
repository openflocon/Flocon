package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

data class FloconScrollAdapterDesktop(
    val scrollbarAdapter: ScrollbarAdapter
) : FloconScrollAdapter

@Composable
actual fun FloconVerticalScrollbar(
    adapter: FloconScrollAdapter,
    modifier: androidx.compose.ui.Modifier
) {
    VerticalScrollbar(
        adapter = (adapter as FloconScrollAdapterDesktop).scrollbarAdapter,
        modifier = modifier
    )
}

@Composable
actual fun rememberFloconScrollbarAdapter(scrollState: LazyListState): FloconScrollAdapter {
    val adapter = rememberScrollbarAdapter(scrollState)
    return remember(adapter) {
        FloconScrollAdapterDesktop(
            adapter
        )
    }
}
