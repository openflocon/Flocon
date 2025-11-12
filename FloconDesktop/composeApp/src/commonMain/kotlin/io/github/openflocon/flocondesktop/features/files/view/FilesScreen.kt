package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.files.FilesViewModel
import io.github.openflocon.flocondesktop.features.files.model.FileUiModel
import io.github.openflocon.flocondesktop.features.files.model.FilesStateUiModel
import io.github.openflocon.flocondesktop.features.files.model.previewFilesStateUiModel
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
        onFilterTextChanged = viewModel::onFilterTextChanged,
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
    onContextualAction: (FileUiModel, FileUiModel.ContextualAction.Action) -> Unit,
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
            filterBar = {
                FilterBar(
                    filterText = filterText,
                    placeholderText = "Filter files",
                    onTextChange = onFilterTextChanged,
                    modifier = Modifier.width(250.dp),
                )
            },
            onDeleteContent = onDeleteContent,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(FloconTheme.shapes.medium)
                .background(FloconTheme.colorPalette.primary)
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
            onContextualAction = { _, _ -> },
            filterText = mutableStateOf(""),
            onFilterTextChanged = {},
        )
    }
}
