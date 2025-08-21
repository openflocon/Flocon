package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
actual fun FloconVerticalScrollbar(
    adapter: FloconScrollAdapter,
    modifier: Modifier
) {
}

@Composable
actual fun rememberFloconScrollbarAdapter(scrollState: LazyListState): FloconScrollAdapter {
    return remember { object : FloconScrollAdapter{} }
}
