package io.github.openflocon.flocondesktop.features.network.list.view.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.paging.compose.LazyPagingItems
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkUiState

/** Up / Down to move the selected line, Cmd+F to open the deep search. */
fun Modifier.networkKeyboardNavigation(
    rows: LazyPagingItems<NetworkItemViewState>,
    uiState: NetworkUiState,
    onAction: (NetworkAction) -> Unit,
): Modifier = onPreviewKeyEvent { event ->
    if (event.type != KeyEventType.KeyDown) {
        return@onPreviewKeyEvent false
    }

    when (event.key) {
        Key.DirectionUp -> {
            selectPreviousRow(rows, uiState)
                ?.let { onAction(NetworkAction.Up(it.uuid)) }
            true
        }

        Key.DirectionDown -> {
            selectNextRow(rows, uiState)
                ?.let { onAction(NetworkAction.Down(it.uuid)) }
            true
        }

        Key.F -> {
            if (event.isMetaPressed) {
                onAction(NetworkAction.OpenDeepSearch)
                true
            } else {
                false
            }
        }

        else -> false
    }
}

private fun selectPreviousRow(
    rows: LazyPagingItems<NetworkItemViewState>,
    uiState: NetworkUiState
): NetworkItemViewState? =
    rows.itemSnapshotList.indexOfFirst { it?.uuid == uiState.contentState.selectedRequestId }
        .takeIf { it != -1 }
        ?.let { selectedIndex ->
            val newIndex = if (uiState.settings.invertList) {
                selectedIndex + 1
            } else {
                selectedIndex - 1
            }
            newIndex.takeIf { it > 0 && it <= rows.itemSnapshotList.lastIndex }
        }?.let {
            rows[it]
        }

private fun selectNextRow(
    rows: LazyPagingItems<NetworkItemViewState>,
    uiState: NetworkUiState
): NetworkItemViewState? =
    rows.itemSnapshotList.indexOfFirst { it?.uuid == uiState.contentState.selectedRequestId }
        .takeIf { it != -1 }
        ?.let { selectedIndex ->
            val newIndex = if (uiState.settings.invertList) {
                selectedIndex - 1
            } else {
                selectedIndex + 1
            }
            newIndex.takeIf { it > 0 && it <= rows.itemSnapshotList.lastIndex }
        }?.let {
            rows[it]
        }
