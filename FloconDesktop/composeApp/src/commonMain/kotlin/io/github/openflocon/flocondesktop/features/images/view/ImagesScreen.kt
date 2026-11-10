package io.github.openflocon.flocondesktop.features.images.view

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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.images_delete
import flocondesktop.composeapp.generated.resources.images_display_grid
import flocondesktop.composeapp.generated.resources.images_display_list
import flocondesktop.composeapp.generated.resources.images_filter
import flocondesktop.composeapp.generated.resources.menu_item_images
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocButton
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureEmptyState
import io.github.openflocon.flocondesktop.features.images.ImagesViewModel
import io.github.openflocon.flocondesktop.features.images.model.ImageViewerUiModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesDisplayModeUiModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesStateUiModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import io.github.openflocon.flocondesktop.features.images.model.previewImagesStateUiModel
import io.github.openflocon.flocondesktop.features.images.view.viewer.ImageViewer
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconSegmentedIconOption
import io.github.openflocon.library.designsystem.components.FloconSegmentedIconToggle
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.floconPanel
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import io.github.openflocon.navigation.mascot.mascotPlatform
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private val GridMinCellSize = 250.dp

@Composable
fun ImagesScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: ImagesViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val displayMode by viewModel.displayMode.collectAsStateWithLifecycle()
    val viewer by viewModel.viewer.collectAsStateWithLifecycle()

    DisposableEffect(viewModel) {
        viewModel.onVisible()
        onDispose {
            viewModel.onNotVisible()
        }
    }

    ImagesScreen(
        state = state,
        displayMode = displayMode,
        onDisplayModeChanged = viewModel::onDisplayModeChanged,
        onReset = viewModel::reset,
        onClickImage = { viewModel.onImageSelected(it.id) },
        onFilterChanged = viewModel::onFilterChanged,
        viewer = viewer,
        onImageSelected = viewModel::onImageSelected,
        onCopyImageUrl = viewModel::onCopyImageUrl,
        onDownloadImage = viewModel::onDownloadImage,
        onViewerClosed = viewModel::onViewerClosed,
        modifier = modifier,
    )
}

@Composable
private fun ImagesScreen(
    state: ImagesStateUiModel,
    displayMode: ImagesDisplayModeUiModel,
    onDisplayModeChanged: (ImagesDisplayModeUiModel) -> Unit,
    onReset: () -> Unit,
    onFilterChanged: (text: String) -> Unit,
    onClickImage: (ImagesUiModel) -> Unit,
    viewer: ImageViewerUiModel?,
    onImageSelected: (id: String) -> Unit,
    onCopyImageUrl: (url: String) -> Unit,
    onDownloadImage: (ImagesUiModel) -> Unit,
    onViewerClosed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentFilter by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier
    ) {
        FloconPageTopBar(
            modifier = Modifier
                .fillMaxWidth()
                .mascotPlatform(),
            trailing = { FeatureDocButton(doc = FeatureDoc.Images) },
            filterBar = {
                FilterBar(
                    placeholderText = stringResource(Res.string.images_filter),
                    onTextChange = {
                        currentFilter = it
                        onFilterChanged(it)
                    },
                    modifier = Modifier.weight(1f),
                )
            },
            actions = {
                // same [list | grid] toggle as the Files screen
                val listTooltip = stringResource(Res.string.images_display_list)
                val gridTooltip = stringResource(Res.string.images_display_grid)
                FloconSegmentedIconToggle(
                    options = remember(listTooltip, gridTooltip) {
                        persistentListOf(
                            FloconSegmentedIconOption(icon = Icons.AutoMirrored.Outlined.List, tooltip = listTooltip),
                            FloconSegmentedIconOption(icon = Icons.Outlined.GridView, tooltip = gridTooltip),
                        )
                    },
                    selectedIndex = displayMode.ordinal,
                    onSelect = { onDisplayModeChanged(ImagesDisplayModeUiModel.entries[it]) },
                )
                FloconIconButton(
                    imageVector = Icons.Outlined.Delete,
                    tooltip = stringResource(Res.string.images_delete),
                    onClick = onReset,
                )
            }
        )
        when (state) {
            ImagesStateUiModel.Idle -> Box(Modifier)

            ImagesStateUiModel.Empty -> if (currentFilter.isEmpty()) {
                FeatureEmptyState(
                    doc = FeatureDoc.Images,
                    modifier = Modifier
                        .fillMaxSize()
                        .floconPanel()
                )
            }

            is ImagesStateUiModel.WithImages -> when (displayMode) {
                ImagesDisplayModeUiModel.Grid -> ImagesGrid(
                    images = state.images,
                    onClickImage = onClickImage,
                    modifier = Modifier.fillMaxSize(),
                )

                ImagesDisplayModeUiModel.List -> ImagesList(
                    images = state.images,
                    onClickImage = onClickImage,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
    viewer?.let {
        ImageViewer(
            model = it,
            onImageSelected = onImageSelected,
            onCopyUrl = onCopyImageUrl,
            onDownload = onDownloadImage,
            onClose = onViewerClosed,
        )
    }
}

@Composable
private fun ImagesGrid(
    images: ImmutableList<ImagesUiModel>,
    onClickImage: (ImagesUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()
    val scrollAdapter = rememberFloconScrollbarAdapter(gridState)
    val gridPadding = FloconTheme.spacing.small

    Box(modifier = modifier) {
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Adaptive(minSize = GridMinCellSize),
            horizontalArrangement = Arrangement.spacedBy(gridPadding),
            verticalArrangement = Arrangement.spacedBy(gridPadding),
            contentPadding = PaddingValues(all = gridPadding),
            modifier = Modifier
                .matchParentSize()
                .floconPanel(),
        ) {
            items(items = images, key = { it.id }) {
                ImageItem(
                    model = it,
                    onClick = onClickImage,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        FloconVerticalScrollbar(
            adapter = scrollAdapter,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd),
        )
    }
}

@Composable
private fun ImagesList(
    images: ImmutableList<ImagesUiModel>,
    onClickImage: (ImagesUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(listState)

    Box(modifier = modifier) {
        Column(modifier = Modifier.matchParentSize().floconPanel()) {
            ImageListHeader()
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(vertical = FloconTheme.spacing.small),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                itemsIndexed(items = images, key = { _, item -> item.id }) { index, item ->
                    if (index > 0) {
                        FloconHorizontalDivider()
                    }
                    ImageListItem(
                        model = item,
                        onClick = onClickImage,
                    )
                }
            }
        }
        FloconVerticalScrollbar(
            adapter = scrollAdapter,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd),
        )
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview
private fun ImagesListScreenPreview() {
    FloconTheme {
        ImagesScreen(
            displayMode = ImagesDisplayModeUiModel.List,
            onDisplayModeChanged = {},
            onReset = {},
            onClickImage = {},
            onFilterChanged = {},
            viewer = null,
            onImageSelected = {},
            onCopyImageUrl = {},
            onDownloadImage = {},
            onViewerClosed = {},
            state = previewImagesStateUiModel(),
        )
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview
private fun ImagesScreenPreview() {
    FloconTheme {
        ImagesScreen(
            displayMode = ImagesDisplayModeUiModel.Grid,
            onDisplayModeChanged = {},
            onReset = {},
            onClickImage = {},
            onFilterChanged = {},
            viewer = null,
            onImageSelected = {},
            onCopyImageUrl = {},
            onDownloadImage = {},
            onViewerClosed = {},
            state = previewImagesStateUiModel(),
        )
    }
}
