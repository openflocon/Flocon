package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier

@Immutable
interface FloconScrollAdapter

@Composable
expect fun rememberFloconScrollbarAdapter(scrollState: LazyListState): FloconScrollAdapter

@Composable
expect fun rememberFloconScrollbarAdapter(scrollState: ScrollState): FloconScrollAdapter

@Composable
expect fun rememberFloconScrollbarAdapter(scrollState: LazyGridState): FloconScrollAdapter

@Composable
expect fun FloconVerticalScrollbar(
    adapter: FloconScrollAdapter,
    modifier: Modifier = Modifier,
    style: ScrollbarStyle = LocalScrollbarStyle.current,
)

/**
 * An adapter for a content that is not a scrollable container (a time axis, a canvas): the sizes and the offset are in px,
 * read when the scrollbar needs them, [scrollTo] is called while the thumb is dragged or the track clicked.
 */
@Composable
expect fun rememberFloconScrollbarAdapter(
    scrollOffset: () -> Double,
    contentSize: () -> Double,
    viewportSize: () -> Double,
    scrollTo: suspend (Double) -> Unit,
): FloconScrollAdapter

@Composable
expect fun FloconHorizontalScrollbar(
    adapter: FloconScrollAdapter,
    modifier: Modifier = Modifier,
    style: ScrollbarStyle = LocalScrollbarStyle.current,
)
