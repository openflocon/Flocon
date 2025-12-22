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
import io.github.openflocon.flocondesktop.features.files.model.FileColumnUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileUiModel
import io.github.openflocon.flocondesktop.features.files.model.FilesStateUiModel
import io.github.openflocon.flocondesktop.features.files.model.path
import io.github.openflocon.flocondesktop.features.files.model.previewFilesStateUiModel
import io.github.openflocon.flocondesktop.features.files.view.header.FilesListHeader
import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel
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
        onFileClicked = viewModel::onFileClicked,
        onNavigateUp = viewModel::onNavigateUp,
        onContextualAction = viewModel::onContextualAction,
        onRefresh = viewModel::onRefresh,
        onDeleteContent = viewModel::onDeleteContent,
        filterText = filterText,
        clickOnSort = viewModel::clickOnSort,
        onFilterTextChanged = viewModel::onFilterTextChanged,
        updateWithFoldersSize = viewModel::updateWithFoldersSize,
        onToggleMultiSelection = viewModel::toggleMultiSelection,
        onDeleteSelectedFiles = viewModel::deleteSelectedFiles,
        onSelectionChange = viewModel::onFileSelectionChanged,
    )
}

@Composable
private fun FilesScreen(
    state: FilesStateUiModel,
    filterText: State<String>,
    onFilterTextChanged: (String) -> Unit,
    onNavigateUp: () -> Unit,
    onRefresh: () -> Unit,
    onDeleteContent: () -> Unit,
    onFileClicked: (FileUiModel) -> Unit,
    updateWithFoldersSize: (Boolean) -> Unit,
    onToggleMultiSelection: () -> Unit,
    onDeleteSelectedFiles: () -> Unit,
    onSelectionChange: (FileUiModel, Boolean, Boolean) -> Unit,
    onContextualAction: (FileUiModel, FileUiModel.ContextualAction.Action) -> Unit,
    clickOnSort: (FileColumnUiModel, SortedByUiModel) -> Unit,
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
            onBack = onNavigateUp,
            onRefresh = onRefresh,
            options = state.options,
            filterBar = {
                FilterBar(
                    filterText = filterText,
                    placeholderText = "Filter files",
                    onTextChange = onFilterTextChanged,
                    modifier = Modifier.width(250.dp),
                )
            },
            onDeleteContent = onDeleteContent,
            updateWithFoldersSize = updateWithFoldersSize,
            onToggleMultiSelection = onToggleMultiSelection,
            onDeleteSelectedFiles = onDeleteSelectedFiles,
            multiSelectionEnabled = state.multiSelection.enabled,
            selectedCount = state.multiSelection.selectedPaths.size,
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
                clickOnSort = clickOnSort,
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
                        FileItemRow(
                            item,
                            onClick = onFileClicked,
                            modifier = Modifier.fillMaxWidth(),
                            onContextualAction = onContextualAction,
                            selected = state.multiSelection.selectedPaths.contains(item.path.path),
                            selectionEnabled = state.multiSelection.enabled,
                            onSelectionChange = { selected, shiftPressed ->
                                onSelectionChange(item, selected, shiftPressed)
                            }
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
            onNavigateUp = {},
            onFileClicked = {},
            onRefresh = {},
            onDeleteContent = {},
            updateWithFoldersSize = {},
            onToggleMultiSelection = {},
            onDeleteSelectedFiles = {},
            onSelectionChange = { _, _, _ -> },
            onContextualAction = { _, _ -> },
            filterText = mutableStateOf(""),
            onFilterTextChanged = {},
            clickOnSort = { _, _ -> },
        )
    }
}
