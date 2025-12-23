package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.files.FilesViewModel
import io.github.openflocon.flocondesktop.features.files.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.model.FilesAction
import io.github.openflocon.flocondesktop.features.files.model.FilesStateUiModel
import io.github.openflocon.flocondesktop.features.files.model.previewFilesStateUiModel
import io.github.openflocon.flocondesktop.features.files.view.header.FilesListHeader
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FilesScreen(modifier: Modifier = Modifier) {
    val viewModel: FilesViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filterText = viewModel.filterText

    DisposableEffect(viewModel) {
        viewModel.onVisible()
        onDispose {
            viewModel.onNotVisible()
        }
    }
    FilesScreen(
        state = state,
        modifier = modifier,
        filterText = filterText,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun FilesScreen(
    state: FilesStateUiModel,
    filterText: State<String>,
    onAction: (FilesAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(listState)

    FloconFeature(
        modifier = modifier.fillMaxSize()
    ) {
        FilesTopBar(
            modifier = Modifier.fillMaxWidth(),
            current = state.current,
            numberOfFiles = state.numberOfFiles,
            onBack = { onAction(FilesAction.NavigateUp) },
            onRefresh = { onAction(FilesAction.Refresh) },
            options = state.options,
            selecting = state.isSelecting,
            canSelect = state.canSelect,
            selectedCount = state.multiSelectedPaths.size,
            filterBar = {
                FilterBar(
                    filterText = filterText,
                    placeholderText = "Filter files",
                    onTextChange = { onAction(FilesAction.FilterTextChanged(it)) },
                    modifier = Modifier.width(250.dp),
                )
            },
            onDeleteContent = { onAction(FilesAction.DeleteContent) },
            updateWithFoldersSize = { onAction(FilesAction.UpdateWithFoldersSize(it)) },
            onDeleteSelection = { onAction(FilesAction.DeleteSelection) },
            onMultiSelect = { onAction(FilesAction.MultiSelect) },
            onClearMultiSelect = { onAction(FilesAction.ClearMultiSelect) },
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(FloconTheme.shapes.medium)
                .background(FloconTheme.colorPalette.primary),
        ) {
            FilesListHeader(
                modifier = Modifier.fillMaxWidth(),
                state = state.headerState,
                clickOnSort = { column, sortedBy -> onAction(FilesAction.ClickOnSort(column, sortedBy)) },
                isSelecting = state.isSelecting,
                selectingAll = state.selectingAll,
                onSelectAll = { onAction(FilesAction.SelectAll(it)) },
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    itemsIndexed(state.files) { index, item ->
                        val path = (item.path as? FilePathUiModel.Real)?.absolutePath
                        FileItemRow(
                            file = item,
                            index = index,
                            multiSelect = state.isSelecting,
                            multiSelected = path != null && state.multiSelectedPaths.contains(path),
                            onClick = { onAction(FilesAction.FileClicked(it)) },
                            modifier = Modifier.fillMaxWidth(),
                            onContextualAction = { file, action -> onAction(FilesAction.ContextualAction(file, action)) },
                            onSelectFile = { p, selected, i, shiftHeld -> 
                                onAction(FilesAction.SelectFile(p, selected, i, shiftHeld)) 
                            },
                        )
                        if (index != state.files.lastIndex) {
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
                FloconVerticalScrollbar(
                    adapter = scrollAdapter,
                    modifier = Modifier.fillMaxHeight()
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Composable
@Preview
private fun FilesScreenPreview() {
    FloconTheme {
        FilesScreen(
            state = previewFilesStateUiModel(),
            filterText = mutableStateOf(""),
            onAction = {},
        )
    }
}
