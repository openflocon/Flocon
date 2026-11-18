package io.github.openflocon.flocondesktop.features.network.list.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureEmptyState
import io.github.openflocon.flocondesktop.features.network.body.model.ContentUiState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemColumnWidths
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.view.NetworkItem
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter

@Composable
fun NetworkRowsList(
    rows: LazyPagingItems<NetworkItemViewState>,
    contentState: ContentUiState,
    invertList: Boolean,
    showEmptyState: Boolean,
    columnWidths: NetworkItemColumnWidths,
    lazyListState: LazyListState,
    onAction: (NetworkAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollAdapter = rememberFloconScrollbarAdapter(lazyListState)

    Row(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            reverseLayout = invertList,
            contentPadding = PaddingValues(
                horizontal = FloconTheme.spacing.small,
                vertical = FloconTheme.spacing.extraSmall,
            ),
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            items(
                count = rows.itemCount,
                key = rows.itemKey { it.uuid },
            ) { index ->
                val item = rows[index]
                if (item != null) {
                    NetworkItem(
                        state = item,
                        selected = item.uuid == contentState.selectedRequestId,
                        columnWidths = columnWidths,
                        onAction = onAction,
                        multiSelect = contentState.selecting,
                        multiSelected = contentState.multiSelectedIds.contains(item.uuid),
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(),
                    )
                } else {
                    Box(Modifier) // display nothing during load
                }
            }
        }
        if (showEmptyState) {
            FeatureEmptyState(
                doc = FeatureDoc.Network,
                modifier = Modifier.fillMaxSize(),
            )
        }
        this@Row.AnimatedVisibility(
            visible = contentState.selecting,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier.align(Alignment.Bottom)
        ) {
            NetworkSelectionBar(
                selectedCount = contentState.multiSelectedIds.size,
                onAction = onAction,
            )
        }
        FloconVerticalScrollbar(
            adapter = scrollAdapter,
            modifier = Modifier.fillMaxHeight(),
        )
    }
}
