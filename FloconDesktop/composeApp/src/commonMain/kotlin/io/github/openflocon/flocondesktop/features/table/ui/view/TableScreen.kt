package io.github.openflocon.flocondesktop.features.table.ui.view

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.florent37.flocondesktop.common.ui.FloconColors
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.features.table.ui.TableViewModel
import com.florent37.flocondesktop.features.table.ui.model.DeviceTableUiModel
import com.florent37.flocondesktop.features.table.ui.model.TableContentStateUiModel
import com.florent37.flocondesktop.features.table.ui.model.TableRowUiModel
import com.florent37.flocondesktop.features.table.ui.model.TablesStateUiModel
import com.florent37.flocondesktop.features.table.ui.model.items
import com.florent37.flocondesktop.features.table.ui.model.previewTableContentStateUiModel
import com.florent37.flocondesktop.features.table.ui.model.previewTablesStateUiModel
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

    Surface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .background(FloconColors.pannel)
                        .padding(all = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Tables",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
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

                Surface(
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

            selectedItem?.let {
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
            .background(FloconColors.background)
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
            style = MaterialTheme.typography.bodyMedium, // Body text for the URL
            color = MaterialTheme.colorScheme.onBackground, // Primary text color
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
            style = MaterialTheme.typography.titleSmall, // Slightly smaller title for details
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), // Muted label color
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
