package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface FloconScrollAdapter

@Composable
expect fun rememberFloconScrollbarAdapter(scrollState: LazyListState) : FloconScrollAdapter

@Composable
expect fun FloconVerticalScrollbar(
    adapter: FloconScrollAdapter,
    modifier: Modifier = Modifier
)
