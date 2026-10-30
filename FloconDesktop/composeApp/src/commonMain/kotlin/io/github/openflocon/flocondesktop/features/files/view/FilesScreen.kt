package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureEmptyState
import io.github.openflocon.flocondesktop.features.files.FilesViewModel
import io.github.openflocon.flocondesktop.features.files.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileUiModel
import io.github.openflocon.flocondesktop.features.files.model.FilesAction
import io.github.openflocon.flocondesktop.features.files.model.FilesStateUiModel
import io.github.openflocon.flocondesktop.features.files.model.FilesViewModeUiModel
import io.github.openflocon.flocondesktop.features.files.model.previewFilesStateUiModel
import io.github.openflocon.flocondesktop.features.files.view.header.FilesGridSortBar
import io.github.openflocon.flocondesktop.features.files.view.header.FilesListHeader
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.floconPanel
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import io.github.openflocon.navigation.mascot.mascotPlatform
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
    val onFileClicked = remember(onAction) { { file: FileUiModel -> onAction(FilesAction.FileClicked(file)) } }
    val onContextualAction = remember(onAction) {
        { file: FileUiModel, action: FileUiModel.ContextualAction.Action ->
            onAction(FilesAction.ContextualAction(file, action))
        }
    }
    val onSelectFile = remember(onAction) {
        { path: String, selected: Boolean, index: Int, shiftHeld: Boolean ->
            onAction(FilesAction.SelectFile(path, selected, index, shiftHeld))
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier.fillMaxSize(),
    ) {
        FilesTopBar(
            state = state,
            filterText = filterText,
            onAction = onAction,
            modifier = Modifier
                .fillMaxWidth()
                .mascotPlatform(),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .floconPanel(),
        ) {
            when (state.viewMode) {
                FilesViewModeUiModel.List -> FilesListHeader(
                    state = state.headerState,
                    clickOnSort = { column, sortedBy -> onAction(FilesAction.ClickOnSort(column, sortedBy)) },
                    isSelecting = state.isSelecting,
                    selectingAll = state.selectingAll,
                    onSelectAll = { onAction(FilesAction.SelectAll(it)) },
                )

                FilesViewModeUiModel.Grid -> FilesGridSortBar(
                    state = state.headerState,
                    clickOnSort = { column, sortedBy -> onAction(FilesAction.ClickOnSort(column, sortedBy)) },
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                when (state.viewMode) {
                    FilesViewModeUiModel.List -> FilesList(
                        state = state,
                        onFileClicked = onFileClicked,
                        onContextualAction = onContextualAction,
                        onSelectFile = onSelectFile,
                    )

                    FilesViewModeUiModel.Grid -> FilesGrid(
                        state = state,
                        onFileClicked = onFileClicked,
                        onContextualAction = onContextualAction,
                        onSelectFile = onSelectFile,
                    )
                }
                if (state.showEmptyState) {
                    FeatureEmptyState(
                        doc = FeatureDoc.Files,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            FilesFooter(
                footer = state.footer,
                totalSizeFormatted = state.headerState.totalSizeFormatted,
            )
        }
    }
}

private fun FilesStateUiModel.isMultiSelected(path: FilePathUiModel): Boolean = (path as? FilePathUiModel.Real)?.absolutePath?.let { it in multiSelectedPaths } == true

@Composable
private fun FilesList(
    state: FilesStateUiModel,
    onFileClicked: (FileUiModel) -> Unit,
    onContextualAction: (FileUiModel, FileUiModel.ContextualAction.Action) -> Unit,
    onSelectFile: (path: String, selected: Boolean, index: Int, shiftHeld: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(listState)
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(state.files, key = { _, item -> item.id }) { index, item ->
                FileItemRow(
                    file = item,
                    index = index,
                    multiSelect = state.isSelecting,
                    multiSelected = state.isMultiSelected(item.path),
                    onClick = onFileClicked,
                    onContextualAction = onContextualAction,
                    onSelectFile = onSelectFile,
                )
            }
        }
        FloconVerticalScrollbar(
            adapter = scrollAdapter,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.TopEnd),
        )
    }
}

@Composable
private fun FilesGrid(
    state: FilesStateUiModel,
    onFileClicked: (FileUiModel) -> Unit,
    onContextualAction: (FileUiModel, FileUiModel.ContextualAction.Action) -> Unit,
    onSelectFile: (path: String, selected: Boolean, index: Int, shiftHeld: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()
    val scrollAdapter = rememberFloconScrollbarAdapter(gridState)
    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = FileGridCellMinWidth),
            state = gridState,
            contentPadding = PaddingValues(
                horizontal = FloconTheme.spacing.large + FloconTheme.spacing.extraSmall,
                vertical = FloconTheme.spacing.extraLarge,
            ),
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraLarge),
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(state.files, key = { _, item -> item.id }) { index, item ->
                FileGridItem(
                    file = item,
                    index = index,
                    multiSelect = state.isSelecting,
                    multiSelected = state.isMultiSelected(item.path),
                    // the size of the folders is only known with the "folder sizes" option
                    showSize = !item.isDirectory || state.options.withFoldersSize,
                    onClick = onFileClicked,
                    onContextualAction = onContextualAction,
                    onSelectFile = onSelectFile,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        FloconVerticalScrollbar(
            adapter = scrollAdapter,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.TopEnd),
        )
    }
}

@Composable
@Preview
private fun FilesScreenPreview() {
    FloconTheme {
        FilesScreen(
            state = previewFilesStateUiModel(),
            filterText = remember { mutableStateOf("") },
            onAction = {},
        )
    }
}

@Composable
@Preview
private fun FilesScreenGridPreview() {
    FloconTheme {
        FilesScreen(
            state = previewFilesStateUiModel(viewMode = FilesViewModeUiModel.Grid),
            filterText = remember { mutableStateOf("") },
            onAction = {},
        )
    }
}
