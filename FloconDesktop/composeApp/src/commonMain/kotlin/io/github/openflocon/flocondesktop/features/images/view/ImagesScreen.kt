package io.github.openflocon.flocondesktop.features.images.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import io.github.openflocon.flocondesktop.features.images.ImagesViewModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesStateUiModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import io.github.openflocon.flocondesktop.features.images.model.previewImagesStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ImagesScreen(modifier: Modifier = Modifier) {
    val viewModel: ImagesViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    var clickedImage by remember {
        mutableStateOf<ImagesUiModel?>(null)
    }

    DisposableEffect(viewModel) {
        viewModel.onVisible()
        onDispose {
            viewModel.onNotVisible()
        }
    }
    ImagesScreen(
        state = state,
        onReset = viewModel::reset,
        onClickImage = {
            clickedImage = it
        },
        resetClickedImage = {
            clickedImage = null
        },
        clickedImage = clickedImage,
        modifier = modifier,
    )
}

@Composable
private fun ImagesScreen(
    state: ImagesStateUiModel,
    onReset: () -> Unit,
    onClickImage: (ImagesUiModel) -> Unit,
    resetClickedImage: () -> Unit,
    clickedImage: ImagesUiModel?,
    modifier: Modifier = Modifier,
) {
    FloconSurface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FloconTheme.colorPalette.panel)
                    .padding(vertical = 16.dp, horizontal = 16.dp),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Images",
                        modifier = Modifier.padding(bottom = 12.dp),
                        style = FloconTheme.typography.titleLarge,
                        color = FloconTheme.colorPalette.onSurface,
                    )
                    Box(modifier = Modifier.weight(1f))
                    FloconIconButton(
                        imageVector = Icons.Outlined.Delete,
                        onClick = onReset,
                    )
                }
            }
            when (state) {
                ImagesStateUiModel.Empty,
                ImagesStateUiModel.Idle,
                -> Box(Modifier)

                is ImagesStateUiModel.WithImages -> {
                    val gridPadding = 12.dp

                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Adaptive(minSize = 250.dp),
                        horizontalArrangement = Arrangement.spacedBy(gridPadding),
                        verticalArrangement = Arrangement.spacedBy(gridPadding),
                        contentPadding = PaddingValues(all = gridPadding),
                    ) {
                        items(state.images) {
                            ImageItemView(
                                model = it,
                                onClick = onClickImage,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                        }
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
}

@Composable
fun ImageDialog(model: ImagesUiModel, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SelectionContainer {
                Column(
                    modifier = Modifier
                        .background(Color.Black)
                        .fillMaxWidth()
                        .padding(all = 12.dp),
                ) {
                    Text(text = model.downloadedAt, color = Color.White, fontSize = 14.sp)
                    Text(text = model.url, color = Color.Gray, fontSize = 10.sp)
                }
            }
            AsyncImage(
                model = model.url,
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
@Preview
private fun ImagesScreenPreview() {
    FloconTheme {
        ImagesScreen(
            onReset = {},
            onClickImage = {},
            resetClickedImage = {},
            clickedImage = null,
            state = previewImagesStateUiModel(),
        )
    }
}
