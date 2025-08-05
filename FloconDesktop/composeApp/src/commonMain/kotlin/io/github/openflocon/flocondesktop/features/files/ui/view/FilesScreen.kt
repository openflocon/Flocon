package io.github.openflocon.flocondesktop.features.files.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.files.ui.FilesViewModel
import io.github.openflocon.flocondesktop.features.files.ui.model.FileUiModel
import io.github.openflocon.flocondesktop.features.files.ui.model.FilesStateUiModel
import io.github.openflocon.flocondesktop.features.files.ui.model.previewFilesStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FilesScreen(modifier: Modifier = Modifier) {
    val viewModel: FilesViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

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
    )
}

@Composable
private fun FilesScreen(
    state: FilesStateUiModel,
    onNavigateUp: () -> Unit,
    onRefresh: () -> Unit,
    onDeleteContent: () -> Unit,
    onFileClicked: (FileUiModel) -> Unit,
    onContextualAction: (FileUiModel, FileUiModel.ContextualAction.Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            FilesTopBar(
                modifier = Modifier.fillMaxWidth(),
                current = state.current,
                onBack = onNavigateUp,
                onRefresh = onRefresh,
                onDeleteContent = onDeleteContent,
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    vertical = 12.dp,
                ),
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
        )
    }
}
