package io.github.openflocon.flocondesktop.features.table.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsAction
import io.github.openflocon.flocondesktop.features.table.TableViewModel
import io.github.openflocon.flocondesktop.features.table.model.DeviceTableUiModel
import io.github.openflocon.flocondesktop.features.table.model.TableAction
import io.github.openflocon.flocondesktop.features.table.model.TableContentStateUiModel
import io.github.openflocon.flocondesktop.features.table.model.TableRowUiModel
import io.github.openflocon.flocondesktop.features.table.model.TablesStateUiModel
import io.github.openflocon.flocondesktop.features.table.model.items
import io.github.openflocon.flocondesktop.features.table.model.previewTableContentStateUiModel
import io.github.openflocon.flocondesktop.features.table.model.previewTablesStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconOverflow
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TableScreen(modifier: Modifier = Modifier) {
    val viewModel: TableViewModel = koinViewModel()
    val deviceTables by viewModel.deviceTables.collectAsStateWithLifecycle()
    val rows by viewModel.content.collectAsStateWithLifecycle()

    DisposableEffect(viewModel) {
        viewModel.onVisible()
        onDispose {
            viewModel.onNotVisible()
        }
    }

    TableScreen(
        deviceTables = deviceTables,
        onTableSelected = viewModel::onTableSelected,
        content = rows,
        onResetClicked = viewModel::onResetClicked,
        modifier = modifier,
        onAction = viewModel::onAction,
    )
}

@Composable
fun TableScreen(
    deviceTables: TablesStateUiModel,
    onTableSelected: (DeviceTableUiModel) -> Unit,
    content: TableContentStateUiModel,
    onResetClicked: () -> Unit,
    onAction: (TableAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val columnsWidth = 150.dp
    var tableItems by remember { mutableStateOf<List<TableRowUiModel>>(emptyList()) }
    val listState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(listState)

    FloconFeature(
        modifier = modifier
            .fillMaxSize()
    ) {
        FloconPageTopBar(
            modifier = Modifier.fillMaxWidth(),
            selector = {
                TableSelectorView(
                    tablesState = deviceTables,
                    onTableSelected = onTableSelected,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            filterBar = {
                TableFilterBar(
                    tableItems = content.items(),
                    onResetClicked = onResetClicked,
                    onItemsChange = { tableItems = it },
                )
            },
            actions = {
                FloconOverflow {
                    FloconDropdownMenuItem(
                        text = "Export CSV",
                        leadingIcon = Icons.Outlined.ImportExport,
                        onClick = { onAction(TableAction.ExportCsv) }
                    )
                }
            }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(FloconTheme.shapes.medium)
                .background(FloconTheme.colorPalette.primary)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                stickyHeader {
                    when (content) {
                        is TableContentStateUiModel.Empty -> {}
                        is TableContentStateUiModel.Loading -> {}
                        is TableContentStateUiModel.WithContent -> {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .background(FloconTheme.colorPalette.secondary)
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                            ) {
                                content.columns.columns.fastForEach { column ->
                                    Text(
                                        text = column,
                                        modifier =
                                            Modifier
                                                .width(columnsWidth)
                                                .padding(horizontal = 4.dp),
                                        style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = FloconTheme.colorPalette.onSecondary,
                                    )
                                }
                            }
                        }
                    }
                }
                when (content) {
                    is TableContentStateUiModel.Empty -> {}
                    is TableContentStateUiModel.Loading -> {}
                    is TableContentStateUiModel.WithContent -> {
                        itemsIndexed(tableItems) { index, item ->
                            TableRowView(
                                model = item,
                                columnsWidth = columnsWidth,
                                modifier = Modifier.fillMaxWidth(),
                                onAction = onAction,
                            )
                            if (index < tableItems.lastIndex) {
                                HorizontalDivider(
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .padding(top = 4.dp),
                                )
                            }
                        }
                    }
                }
            }
            FloconVerticalScrollbar(
                adapter = scrollAdapter,
                modifier = Modifier.fillMaxHeight()
                    .align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
@Preview
private fun TableScreenPreview() {
    FloconTheme {
        TableScreen(
            deviceTables = previewTablesStateUiModel(),
            onTableSelected = {},
            onResetClicked = {},
            content = previewTableContentStateUiModel(),
            onAction = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
