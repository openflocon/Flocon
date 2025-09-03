package io.github.openflocon.flocondesktop.features.table.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconPanel
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TableScreen(modifier: Modifier = Modifier) {
    val viewModel: TableViewModel = koinViewModel()
    val deviceTables by viewModel.deviceTables.collectAsStateWithLifecycle()
    val rows by viewModel.content.collectAsStateWithLifecycle()
    val selectedItem by viewModel.selectedItem.collectAsStateWithLifecycle()

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
        selectedItem = selectedItem,
        onResetClicked = viewModel::onResetClicked,
        modifier = modifier,
        onTableAction = viewModel::onTableAction,
    )
}

@Composable
fun TableScreen(
    deviceTables: TablesStateUiModel,
    onTableSelected: (DeviceTableUiModel) -> Unit,
    content: TableContentStateUiModel,
    onResetClicked: () -> Unit,
    selectedItem: TableRowUiModel?,
    onTableAction: (TableAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val columnsWidth = 150.dp
    var tableItems by remember { mutableStateOf<List<TableRowUiModel>>(emptyList()) }
    val listState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(listState)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        FloconFeature(
            modifier = modifier
                .clickable(
                    interactionSource = null,
                    indication = null,
                    enabled = selectedItem != null,
                    onClick = { onTableAction(TableAction.ClosePanel) }
                )
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
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(FloconTheme.shapes.medium)
                    .background(FloconTheme.colorPalette.primary)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(all = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    when (content) {
                        is TableContentStateUiModel.Empty -> {}
                        is TableContentStateUiModel.Loading -> {}
                        is TableContentStateUiModel.WithContent -> {
                            itemsIndexed(tableItems) { index, item ->
                                TableRowView(
                                    model = item,
                                    columnsWidth = columnsWidth,
                                    modifier = Modifier.fillMaxWidth(),
                                    onAction = onTableAction,
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
                )
            }
        }
        FloconPanel(
            contentState = selectedItem,
            modifier = Modifier.align(Alignment.CenterEnd),
        ) {
            TableDetailView(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .fillMaxHeight()
                        .width(500.dp),
                state = it,
            )
        }
    }
}

@Composable
fun TableDetailView(modifier: Modifier = Modifier, state: TableRowUiModel) {
    val scrollState = rememberScrollState()
    val linesLabelWidth: Dp = 130.dp
    SelectionContainer(
        modifier
            .background(FloconTheme.colorPalette.primary)
            .verticalScroll(scrollState) // Rendre le contenu défilable
            .padding(all = 18.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            state.values.forEachIndexed { index, value ->
                TableDetailLineTextView(
                    modifier = Modifier.fillMaxWidth(),
                    label = state.columns.getOrNull(index) ?: "",
                    value = value,
                    labelWidth = linesLabelWidth,
                )
            }
        }
    }
}

@Composable
fun TableDetailLineTextView(
    label: String,
    value: String,
    labelWidth: Dp,
    modifier: Modifier = Modifier,
) {
    TableDetailLineView(
        labelWidth = labelWidth,
        label = label,
        modifier = modifier,
    ) {
        Text(
            text = value,
            style = FloconTheme.typography.bodyMedium, // Body text for the URL
            color = FloconTheme.colorPalette.onPrimary, // Primary text color
            modifier = Modifier.weight(1f), // Takes remaining space
        )
    }
}

@Composable
fun TableDetailLineView(
    label: String,
    labelWidth: Dp,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = FloconTheme.typography.titleSmall, // Slightly smaller title for details
            color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.7f), // Muted label color
            modifier = Modifier.width(labelWidth).padding(end = 8.dp),
        )
        content()
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
            selectedItem = null,
            content = previewTableContentStateUiModel(),
            onTableAction = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
