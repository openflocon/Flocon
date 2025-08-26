package io.github.openflocon.flocondesktop.features.analytics.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
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
import io.github.openflocon.flocondesktop.features.analytics.AnalyticsViewModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsAction
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsContentStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsRowUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.DeviceAnalyticsUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.items
import io.github.openflocon.flocondesktop.features.analytics.model.previewAnalyticsContentStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.previewAnalyticsStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconPanel
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AnalyticsScreen(modifier: Modifier = Modifier) {
    val viewModel: AnalyticsViewModel = koinViewModel()
    val deviceAnalytics by viewModel.deviceAnalytics.collectAsStateWithLifecycle()
    val rows by viewModel.content.collectAsStateWithLifecycle()
    val selectedItem by viewModel.selectedItem.collectAsStateWithLifecycle()

    DisposableEffect(viewModel) {
        viewModel.onVisible()
        onDispose {
            viewModel.onNotVisible()
        }
    }
    AnalyticsScreen(
        deviceAnalytics = deviceAnalytics,
        onAnalyticsSelected = viewModel::onAnalyticsSelected,
        content = rows,
        selectedItem = selectedItem,
        onResetClicked = viewModel::onResetClicked,
        modifier = modifier,
        onAction = viewModel::onAction,
    )
}

@Composable
fun AnalyticsScreen(
    deviceAnalytics: AnalyticsStateUiModel,
    onAnalyticsSelected: (DeviceAnalyticsUiModel) -> Unit,
    content: AnalyticsContentStateUiModel,
    onResetClicked: () -> Unit,
    selectedItem: AnalyticsRowUiModel?,
    onAction: (AnalyticsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var analyticsItems by remember { mutableStateOf<List<AnalyticsRowUiModel>>(emptyList()) }

    FloconSurface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                FloconPageTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    selector = {
                        AnalyticsSelectorView(
                            analyticsState = deviceAnalytics,
                            onAnalyticsSelected = onAnalyticsSelected,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    filterBar = {
                        AnalyticsFilterBar(
                            analyticsItems = content.items(),
                            onResetClicked = onResetClicked,
                            onItemsChange = {
                                analyticsItems = it
                            },
                        )
                    }
                )

                FloconSurface(
                    modifier = Modifier.fillMaxSize()
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            enabled = selectedItem != null,
                        ) {
                            // close detail panel
                            onAction(AnalyticsAction.ClosePanel)
                        },
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(all = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        when (content) {
                            is AnalyticsContentStateUiModel.Empty -> {}
                            is AnalyticsContentStateUiModel.Loading -> {}
                            is AnalyticsContentStateUiModel.WithContent -> {
                                itemsIndexed(analyticsItems) { index, item ->
                                    AnalyticsRowView(
                                        model = item,
                                        modifier = Modifier.fillMaxWidth(),
                                        onAction = onAction,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            FloconPanel(
                contentState = selectedItem,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                AnalyticsDetailView(
                    modifier = Modifier.fillMaxSize(),
                    state = it,
                )
            }
        }
    }
}

@Composable
@Preview
private fun AnalyticsScreenPreview() {
    FloconTheme {
        AnalyticsScreen(
            deviceAnalytics = previewAnalyticsStateUiModel(),
            onAnalyticsSelected = {},
            onResetClicked = {},
            selectedItem = null,
            content = previewAnalyticsContentStateUiModel(),
            onAction = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
