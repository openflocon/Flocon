package com.florent37.flocondesktop.features.grpc.ui.view

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
import com.florent37.flocondesktop.common.ui.FloconColors
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.features.grpc.ui.GRPCViewModel
import com.florent37.flocondesktop.features.grpc.ui.model.GrpcDetailViewState
import com.florent37.flocondesktop.features.grpc.ui.model.GrpcItemColumnWidths
import com.florent37.flocondesktop.features.grpc.ui.model.GrpcItemViewState
import com.florent37.flocondesktop.features.grpc.ui.model.OnGrpcItemUserAction
import com.florent37.flocondesktop.features.grpc.ui.model.previewGrpcItemViewState
import com.florent37.flocondesktop.features.grpc.ui.view.header.GrpcFilterBar
import com.florent37.flocondesktop.features.grpc.ui.view.header.GrpcItemHeaderView
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GRPCScreen(modifier: Modifier = Modifier) {
    val viewModel: GRPCViewModel = koinViewModel()
    val state: List<GrpcItemViewState> by viewModel.state.collectAsStateWithLifecycle()
    val detailState: GrpcDetailViewState? by viewModel.detailState.collectAsStateWithLifecycle()

    GRPCScreen(
        grpcItems = state,
        modifier = modifier,
        detailState = detailState,
        onReset = viewModel::onReset,
        closeDetailPanel = viewModel::closeDetailPanel,
        onCopyText = viewModel::onCopyText,
        onGrpcItemUserAction = viewModel::onGrpcItemUserAction,
    )
}

@Composable
private fun GRPCScreen(
    grpcItems: List<GrpcItemViewState>,
    onGrpcItemUserAction: (OnGrpcItemUserAction) -> Unit,
    onReset: () -> Unit,
    detailState: GrpcDetailViewState?,
    closeDetailPanel: () -> Unit,
    onCopyText: (text: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val columnWidths: GrpcItemColumnWidths =
        remember { GrpcItemColumnWidths() } // Default widths provided

    var filteredItems by remember { mutableStateOf<List<GrpcItemViewState>>(emptyList()) }

    Surface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Grpc",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FloconColors.pannel)
                        .padding(all = 12.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                GrpcFilterBar(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(FloconColors.pannel)
                            .padding(horizontal = 12.dp),
                    grpcItems = grpcItems,
                    onResetClicked = onReset,
                    onItemsChange = {
                        filteredItems = it
                    },
                )
                GrpcItemHeaderView(
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
                        GrpcItemView(
                            state = it,
                            columnWidths = columnWidths,
                            modifier = Modifier.fillMaxWidth(),
                            onUserAction = onGrpcItemUserAction,
                        )
                    }
                }
            }
            detailState?.let {
                GrpcDetailView(
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
private fun GRPCScreenPreview() {
    FloconTheme {
        GRPCScreen(
            grpcItems = List(10) {
                previewGrpcItemViewState()
            },
            onReset = {},
            detailState = null,
            closeDetailPanel = {},
            onCopyText = {},
            onGrpcItemUserAction = {},
        )
    }
}
