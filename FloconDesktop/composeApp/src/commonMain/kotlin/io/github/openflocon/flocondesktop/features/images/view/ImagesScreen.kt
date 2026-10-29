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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import flocondesktop.composeapp.generated.resources.images_counter
import flocondesktop.composeapp.generated.resources.images_filter
import flocondesktop.composeapp.generated.resources.menu_item_images
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocButton
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureEmptyState
import io.github.openflocon.flocondesktop.features.images.ImagesViewModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesStateUiModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import io.github.openflocon.flocondesktop.features.images.model.previewImagesStateUiModel
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import io.github.openflocon.navigation.mascot.mascotPlatform
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ImagesScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: ImagesViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    var clickedImage by remember { mutableStateOf<ImagesUiModel?>(null) }

    DisposableEffect(viewModel) {
        viewModel.onVisible()
        onDispose {
            viewModel.onNotVisible()
        }
    }

    ImagesScreen(
        state = state,
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
    onReset: () -> Unit,
    onFilterChanged: (text: String) -> Unit,
    onClickImage: (ImagesUiModel) -> Unit,
    resetClickedImage: () -> Unit,
    clickedImage: ImagesUiModel?,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyGridState()
    val scrollAdapter = rememberFloconScrollbarAdapter(lazyListState)
    var currentFilter by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier
    ) {
        FloconPageTopBar(
            modifier = Modifier
                .fillMaxWidth()
                .mascotPlatform(),
            title = stringResource(Res.string.menu_item_images),
            subtitle = (state as? ImagesStateUiModel.WithImages)?.images?.size?.let {
                pluralStringResource(Res.plurals.images_counter, it, it)
            },
            headerActions = { FeatureDocButton(doc = FeatureDoc.Images) },
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
                FloconIconButton(
                    imageVector = Icons.Outlined.Delete,
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
                        .clip(FloconTheme.shapes.medium)
                        .background(FloconTheme.colors.bg100)
                )
            }

            is ImagesStateUiModel.WithImages -> {
                val gridPadding = FloconTheme.spacing.small

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyVerticalGrid(
                        state = lazyListState,
                        columns = GridCells.Adaptive(minSize = 250.dp),
                        horizontalArrangement = Arrangement.spacedBy(gridPadding),
                        verticalArrangement = Arrangement.spacedBy(gridPadding),
                        contentPadding = PaddingValues(all = gridPadding),
                        modifier = Modifier
                            .matchParentSize()
                            .clip(FloconTheme.shapes.medium)
                            .background(FloconTheme.colors.bg100)
                    ) {
                        items(state.images) {
                            ImageItem(
                                model = it,
                                onClick = onClickImage,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }
                    }
                    FloconVerticalScrollbar(
                        adapter = scrollAdapter,
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.CenterEnd)
                    )
                }
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
private fun ImagesScreenPreview() {
    FloconTheme {
        ImagesScreen(
            onReset = {},
            onClickImage = {},
            resetClickedImage = {},
            onFilterChanged = {},
            clickedImage = null,
            state = previewImagesStateUiModel(),
        )
    }
}
