package io.github.openflocon.flocondesktop.features.graphql.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.common.ui.FloconColors
import io.github.openflocon.flocondesktop.common.ui.FloconTheme
import io.github.openflocon.flocondesktop.features.graphql.ui.GraphQlViewModel
import io.github.openflocon.flocondesktop.features.graphql.ui.model.GraphQlDetailViewState
import io.github.openflocon.flocondesktop.features.graphql.ui.model.GraphQlItemViewState
import io.github.openflocon.flocondesktop.features.graphql.ui.model.OnGraphQlItemUserAction
import io.github.openflocon.flocondesktop.features.graphql.ui.model.previewGraphQlItemViewState
import io.github.openflocon.flocondesktop.features.graphql.ui.view.header.GraphQlFilterBar
import io.github.openflocon.flocondesktop.features.graphql.ui.view.header.GraphQlItemHeaderView
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GraphQlScreen(modifier: Modifier = Modifier) {
    val viewModel: GraphQlViewModel = koinViewModel()
    val items by viewModel.state.collectAsStateWithLifecycle()
    val detailState by viewModel.detailState.collectAsStateWithLifecycle()
    GraphQlScreen(
        graphQlItems = items,
        modifier = modifier,
        detailState = detailState,
        onGraphQlItemUserAction = viewModel::onGraphQlItemUserAction,
        onCopyText = viewModel::onCopyText,
        onReset = viewModel::onReset,
        closeDetailPanel = viewModel::closeDetailPanel,
    )
}

@Composable
fun GraphQlScreen(
    graphQlItems: List<GraphQlItemViewState>,
    detailState: GraphQlDetailViewState?,
    onGraphQlItemUserAction: (OnGraphQlItemUserAction) -> Unit,
    onCopyText: (String) -> Unit,
    closeDetailPanel: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val columnWidths: GraphQlItemColumnWidths =
        remember { GraphQlItemColumnWidths() } // Default widths provided

    var filteredItems by remember { mutableStateOf<List<GraphQlItemViewState>>(emptyList()) }

    Surface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "GraphQl",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FloconColors.pannel)
                        .padding(all = 12.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                GraphQlFilterBar(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(FloconColors.pannel)
                        .padding(horizontal = 12.dp),
                    graphQlItems = graphQlItems,
                    onResetClicked = onReset,
                    onItemsChange = {
                        filteredItems = it
                    },
                )
                GraphQlItemHeaderView(
                    columnWidths = columnWidths,
                    modifier = Modifier.fillMaxWidth(),
                )
                LazyColumn(
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            enabled = detailState != null,
                        ) {
                            closeDetailPanel()
                        },
                ) {
                    items(filteredItems) {
                        GraphQlItemView(
                            state = it,
                            columnWidths = columnWidths,
                            modifier = Modifier.fillMaxWidth(),
                            onUserAction = onGraphQlItemUserAction,
                        )
                    }
                }
            }
            detailState?.let {
                GraphQlDetailView(
                    modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .fillMaxHeight()
                        .width(500.dp),
                    state = it,
                    onCopy = onCopyText,
                )
            }
        }
    }
}

@Composable
@Preview
private fun GraphQlScreenPreview() {
    FloconTheme {
        val graphQlItems =
            remember {
                listOf(
                    previewGraphQlItemViewState(),
                    previewGraphQlItemViewState(),
                )
            }
        GraphQlScreen(
            graphQlItems = graphQlItems,
            detailState = null,
            closeDetailPanel = {},
            onGraphQlItemUserAction = {},
            onCopyText = {},
            onReset = {},
        )
    }
}
