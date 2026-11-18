package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier

data class FloconScrollAdapterDesktop(
    val scrollbarAdapter: ScrollbarAdapter
) : FloconScrollAdapter

@Composable
actual fun FloconVerticalScrollbar(
    adapter: FloconScrollAdapter,
    modifier: Modifier,
    style: ScrollbarStyle
) {
    VerticalScrollbar(
        adapter = (adapter as FloconScrollAdapterDesktop).scrollbarAdapter,
        modifier = modifier,
        style = style,
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

@Composable
actual fun rememberFloconScrollbarAdapter(scrollState: ScrollState): FloconScrollAdapter {
    val adapter = rememberScrollbarAdapter(scrollState)
    return remember(adapter) {
        FloconScrollAdapterDesktop(
            adapter
        )
    }
}

@Composable
actual fun rememberFloconScrollbarAdapter(scrollState: LazyGridState): FloconScrollAdapter {
    val adapter = rememberScrollbarAdapter(scrollState)
    return remember(adapter) {
        FloconScrollAdapterDesktop(
            adapter
        )
    }
}

@Composable
actual fun FloconHorizontalScrollbar(
    adapter: FloconScrollAdapter,
    modifier: Modifier,
    style: ScrollbarStyle
) {
    HorizontalScrollbar(
        adapter = (adapter as FloconScrollAdapterDesktop).scrollbarAdapter,
        modifier = modifier,
        style = style,
    )
}

@Composable
actual fun rememberFloconScrollbarAdapter(
    scrollOffset: () -> Double,
    contentSize: () -> Double,
    viewportSize: () -> Double,
    scrollTo: suspend (Double) -> Unit,
): FloconScrollAdapter {
    // the adapter stays the same while the lambdas change, a drag of the thumb in progress is not interrupted
    val currentOffset by rememberUpdatedState(scrollOffset)
    val currentContent by rememberUpdatedState(contentSize)
    val currentViewport by rememberUpdatedState(viewportSize)
    val currentScrollTo by rememberUpdatedState(scrollTo)
    return remember {
        FloconScrollAdapterDesktop(
            object : ScrollbarAdapter {
                override val scrollOffset: Double get() = currentOffset()
                override val contentSize: Double get() = currentContent()
                override val viewportSize: Double get() = currentViewport()
                override suspend fun scrollTo(scrollOffset: Double) = currentScrollTo(scrollOffset)
            }
        )
    }
}
