package com.florent37.flocondesktop.features.images.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.florent37.flocondesktop.common.ui.FloconColors
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.features.images.ui.ImagesViewModel
import com.florent37.flocondesktop.features.images.ui.model.ImagesStateUiModel
import com.florent37.flocondesktop.features.images.ui.model.ImagesUiModel
import com.florent37.flocondesktop.features.images.ui.model.previewImagesStateUiModel
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.bin
import org.jetbrains.compose.resources.painterResource
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
    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FloconColors.pannel)
                    .padding(vertical = 16.dp, horizontal = 16.dp),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Images",
                        modifier = Modifier.padding(bottom = 12.dp),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Box(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable(onClick = onReset)
                            .padding(all = 8.dp),
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.bin),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                    }
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
