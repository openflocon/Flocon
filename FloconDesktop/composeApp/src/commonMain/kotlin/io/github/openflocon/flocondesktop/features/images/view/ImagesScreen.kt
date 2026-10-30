package io.github.openflocon.flocondesktop.features.images.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.PlatformContext
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
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
import io.github.openflocon.flocondesktop.features.images.model.ImagesDisplayModeUiModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesStateUiModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import io.github.openflocon.flocondesktop.features.images.model.previewImagesStateUiModel
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconIconToggleButton
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.floconPanel
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import io.github.openflocon.navigation.mascot.mascotPlatform
import kotlinx.collections.immutable.ImmutableList
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
    var clickedImage by remember { mutableStateOf<ImagesUiModel?>(null) }

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
        onClickImage = { clickedImage = it },
        resetClickedImage = { clickedImage = null },
        onFilterChanged = viewModel::onFilterChanged,
        clickedImage = clickedImage,
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
    resetClickedImage: () -> Unit,
    clickedImage: ImagesUiModel?,
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
                FloconIconToggleButton(
                    value = displayMode == ImagesDisplayModeUiModel.Grid,
                    onValueChange = { onDisplayModeChanged(ImagesDisplayModeUiModel.Grid) },
                    tooltip = stringResource(Res.string.images_display_grid),
                ) {
                    FloconIcon(imageVector = Icons.Outlined.GridView)
                }
                FloconIconToggleButton(
                    value = displayMode == ImagesDisplayModeUiModel.List,
                    onValueChange = { onDisplayModeChanged(ImagesDisplayModeUiModel.List) },
                    tooltip = stringResource(Res.string.images_display_list),
                ) {
                    FloconIcon(imageVector = Icons.AutoMirrored.Outlined.ViewList)
                }
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
    clickedImage?.let {
        ImageDialog(
            model = it,
            onDismiss = { resetClickedImage() },
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
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = FloconTheme.spacing.small),
            modifier = Modifier
                .matchParentSize()
                .floconPanel(),
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
        FloconVerticalScrollbar(
            adapter = scrollAdapter,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd),
        )
    }
}

@Composable
fun ImageDialog(model: ImagesUiModel, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SelectionContainer {
                Column(
                    modifier = Modifier
                        .background(Color.Black)
                        .fillMaxWidth()
                        .padding(all = FloconTheme.spacing.medium),
                ) {
                    Text(text = model.downloadedAt, color = Color.White, fontSize = FloconTheme.typography.labelLarge.fontSize)
                    Text(text = model.url, color = Color.Gray, fontSize = FloconTheme.typography.labelSmall.fontSize)
                }
            }
            AsyncImage(
                model = remember(model) {
                    ImageRequest.Builder(PlatformContext.INSTANCE)
                        .data(model.url)
                        .httpHeaders(
                            NetworkHeaders.Builder().apply {
                                model.headers.forEach { (key, value) ->
                                    add(key, value)
                                }
                            }.build()
                        )
                        .build()
                },
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }
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
            resetClickedImage = {},
            onFilterChanged = {},
            clickedImage = null,
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
            resetClickedImage = {},
            onFilterChanged = {},
            clickedImage = null,
            state = previewImagesStateUiModel(),
        )
    }
}
