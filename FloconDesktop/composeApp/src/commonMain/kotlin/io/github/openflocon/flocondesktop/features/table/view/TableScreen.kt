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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.table.TableViewModel
import io.github.openflocon.flocondesktop.features.table.model.DeviceTableUiModel
import io.github.openflocon.flocondesktop.features.table.model.TableContentStateUiModel
import io.github.openflocon.flocondesktop.features.table.model.TableRowUiModel
import io.github.openflocon.flocondesktop.features.table.model.TablesStateUiModel
import io.github.openflocon.flocondesktop.features.table.model.items
import io.github.openflocon.flocondesktop.features.table.model.previewTableContentStateUiModel
import io.github.openflocon.flocondesktop.features.table.model.previewTablesStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconPanel
import io.github.openflocon.library.designsystem.components.FloconSurface
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
        onClickItem = viewModel::onClickItem, // TODO click outside
    )
}

@Composable
fun TableScreen(
    deviceTables: TablesStateUiModel,
    onTableSelected: (DeviceTableUiModel) -> Unit,
    content: TableContentStateUiModel,
    onResetClicked: () -> Unit,
    selectedItem: TableRowUiModel?,
    onClickItem: (selected: TableRowUiModel?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val columnsWidth = 150.dp
    var tableItems by remember { mutableStateOf<List<TableRowUiModel>>(emptyList()) }

    FloconSurface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .background(FloconTheme.colorPalette.panel)
                        .padding(all = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Tables",
                        style = FloconTheme.typography.titleLarge,
                        color = FloconTheme.colorPalette.onSurface,
                    )

                    TableSelectorView(
                        tablesState = deviceTables,
                        onTableSelected = onTableSelected,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    TableFilterBar(
                        tableItems = content.items(),
                        onResetClicked = onResetClicked,
                        onItemsChange = {
                            tableItems = it
                        },
                    )
                }

                FloconSurface(
                    modifier = Modifier.fillMaxSize()
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            enabled = selectedItem != null,
                        ) {
                            // close detail panel
                            onClickItem(null)
                        },
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(all = 12.dp),
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
                                        onClick = onClickItem,
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
}

@Composable
fun TableDetailView(modifier: Modifier = Modifier, state: TableRowUiModel) {
    val scrollState = rememberScrollState()
    val linesLabelWidth: Dp = 130.dp
    SelectionContainer(
        modifier
            .background(FloconTheme.colorPalette.background)
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
            color = FloconTheme.colorPalette.onBackground, // Primary text color
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
            color = FloconTheme.colorPalette.onBackground.copy(alpha = 0.7f), // Muted label color
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
            onClickItem = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
