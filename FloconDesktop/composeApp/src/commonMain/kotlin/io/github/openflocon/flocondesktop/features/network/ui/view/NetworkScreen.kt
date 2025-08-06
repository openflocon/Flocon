package io.github.openflocon.flocondesktop.features.network.ui.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.network.ui.NetworkAction
import io.github.openflocon.flocondesktop.features.network.ui.NetworkUiState
import io.github.openflocon.flocondesktop.features.network.ui.NetworkViewModel
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkJsonUi
import io.github.openflocon.flocondesktop.features.network.ui.model.previewGraphQlItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.previewNetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.previewNetworkUiState
import io.github.openflocon.flocondesktop.features.network.ui.view.header.NetworkItemHeaderView
import io.github.openflocon.flocondesktop.features.network.ui.view.header.NetworkFilter
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NetworkScreen(modifier: Modifier = Modifier) {
    val viewModel: NetworkViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NetworkScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
fun NetworkScreen(
    uiState: NetworkUiState,
    onAction: (NetworkAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val columnWidths: NetworkItemColumnWidths =
        remember { NetworkItemColumnWidths() } // Default widths provided

    FloconSurface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = null,
                        indication = null,
                        enabled = uiState.detailState != null,
                        onClick = { onAction(NetworkAction.ClosePanel) }
                    )
            ) {
                Text(
                    text = "Network",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FloconTheme.colorPalette.panel)
                        .padding(all = 12.dp),
                    style = FloconTheme.typography.titleLarge,
                    color = FloconTheme.colorPalette.onSurface,
                )
                NetworkFilter(
                    uiState = uiState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FloconTheme.colorPalette.panel)
                        .padding(horizontal = 12.dp),
                    onAction = onAction
                )
                NetworkItemHeaderView(
                    columnWidths = columnWidths,
                    modifier = Modifier.fillMaxWidth(),
                )
                Box(
                    Modifier
                        .fillMaxSize(),
                ) {
                    LazyColumn(
                        modifier = Modifier.matchParentSize(),
                    ) {
                        items(
                            items = uiState.items,
                            key = NetworkItemViewState::uuid
                        ) {
                            NetworkItemView(
                                state = it,
                                columnWidths = columnWidths,
                                onAction = onAction,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem()
                            )
                        }
                    }
                }
            }
            AnimatedContent(
                targetState = uiState.detailState,
                transitionSpec = {
                    slideIntoContainer(SlideDirection.Start)
                        .togetherWith(slideOutOfContainer(SlideDirection.End))
                },
                contentKey = { it != null },
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier
                    .fillMaxHeight()
                    .requiredWidth(500.dp)
                    .align(Alignment.TopEnd)
            ) {
                if (it != null) {
                    NetworkDetailView(
                        modifier = Modifier.fillMaxSize(),
                        state = it,
                        onAction = onAction
                    )
                } else {
                    Box(Modifier.fillMaxSize())
                }
            }
        }
    }

    val states = remember { mutableStateMapOf<NetworkJsonUi, WindowState>() }


    LaunchedEffect(uiState.contentState.detailJsons) {
        val deletedJson = states.keys.filter { key -> uiState.contentState.detailJsons.none { key.id == it.id } }
        val addedJson = uiState.contentState.detailJsons.filter { key -> states.keys.none { key.id == it.id } }

        deletedJson.forEach { states.remove(it) }
        addedJson.forEach {
            states.put(
                it, WindowState(
                    placement = WindowPlacement.Floating,
                    position = WindowPosition(Alignment.Center)
                )
            )
        }
    }

    uiState.contentState
        .detailJsons
        .forEach { item ->
            val state = states[item]

            if (state != null) {
                NetworkJsonScreen(
                    json = item,
                    state = state,
                    onCloseRequest = { onAction(NetworkAction.CloseJsonDetail(item.id)) }
                )
            }
        }
}

@Composable
@Preview
private fun NetworkScreenPreview() {
    FloconTheme {
        val uiState = previewNetworkUiState().copy(
            items = remember {
                listOf(
                    previewNetworkItemViewState(),
                    previewNetworkItemViewState(),
                    previewGraphQlItemViewState(),
                    previewNetworkItemViewState(),
                    previewGraphQlItemViewState(),
                    previewNetworkItemViewState(),
                )
            }
        )

        NetworkScreen(
            uiState = uiState,
            onAction = {}
        )
    }
}
