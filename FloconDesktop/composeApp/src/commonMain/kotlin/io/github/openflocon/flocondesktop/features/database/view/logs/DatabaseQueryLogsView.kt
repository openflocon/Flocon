package io.github.openflocon.flocondesktop.features.database.view.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import io.github.openflocon.flocondesktop.common.ui.ContextualView
import io.github.openflocon.flocondesktop.features.database.DatabaseQueryLogsViewModel
import io.github.openflocon.flocondesktop.features.database.model.FilterChipUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconIconToggleButton
import io.github.openflocon.library.designsystem.components.FloconOverflow
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DatabaseQueryLogsView(
    dbName: String,
    modifier: Modifier = Modifier
) {
    val viewModel: DatabaseQueryLogsViewModel = koinViewModel(
        key = dbName,
        parameters = { parametersOf(dbName) }
    )

    val logs = viewModel.logs.collectAsLazyPagingItems()
    val showTransactions by viewModel.showTransactions.collectAsStateWithLifecycle()
    val filterChips by viewModel.filterChips.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        DatabaseLogsHeader(
            modifier = Modifier.fillMaxWidth().border(
                width = 1.dp,
                color = FloconTheme.colorPalette.secondary,
                shape = FloconTheme.shapes.medium
            ),
            searchQuery = searchQuery,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            toggleShowTransactions = viewModel::toggleShowTransactions,
            removeFilterChip = viewModel::removeFilterChip,
            addIncludeFilter = viewModel::addIncludeFilter,
            addExcludeFilter = viewModel::addExcludeFilter,
            toggleFilterType = viewModel::toggleFilterType,
            showTransactions = showTransactions,
            filterChips = filterChips,
            exportToCsv = viewModel::exportToCsv,
            exportToMarkdown = viewModel::exportToMarkdown,
        )

        Box(
            modifier = Modifier.fillMaxSize().padding(top = 12.dp)
                .clip(FloconTheme.shapes.medium)
                .border(
                    width = 1.dp,
                    color = FloconTheme.colorPalette.secondary,
                    shape = FloconTheme.shapes.medium
                )
                .background(
                    color = FloconTheme.colorPalette.primary,
                    shape = FloconTheme.shapes.medium
                )
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(logs.itemCount) { index ->
                    val log = logs[index]
                    if (log != null) {
                        ContextualView(
                            items = listOf(
                                FloconContextMenuItem.Item("Copy Query") {
                                    viewModel.copyQuery(log.sqlQuery)
                                },
                                FloconContextMenuItem.Item("Copy Args") {
                                    viewModel.copyArgs(log.bindArgs)
                                },
                                FloconContextMenuItem.Item("Copy as SQL") {
                                    viewModel.copyAsSql(log.sqlQuery, log.bindArgs)
                                },
                                FloconContextMenuItem.Separator(),
                                FloconContextMenuItem.Item("Filter In") {
                                    viewModel.addFilter(
                                        log.sqlQuery,
                                        FilterChipUiModel.FilterType.INCLUDE
                                    )
                                },
                                FloconContextMenuItem.Item("Filter Out") {
                                    viewModel.addFilter(
                                        log.sqlQuery,
                                        FilterChipUiModel.FilterType.EXCLUDE
                                    )
                                }
                            )
                        ) {
                            DatabaseQueryLogItemView(
                                log = log,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                            )
                        }
                        HorizontalDivider(color = FloconTheme.colorPalette.secondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun DatabaseLogsHeader(
    searchQuery: String,
    showTransactions: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    addIncludeFilter: () -> Unit,
    addExcludeFilter: () -> Unit,
    toggleShowTransactions: () -> Unit,
    toggleFilterType: (FilterChipUiModel) -> Unit,
    removeFilterChip: (FilterChipUiModel) -> Unit,
    exportToCsv: () -> Unit,
    exportToMarkdown: () -> Unit,
    filterChips: List<FilterChipUiModel>,
    modifier: Modifier = Modifier,
) {
    FloconPageTopBar(modifier) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloconTextFieldWithoutM3(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        modifier = Modifier.weight(1f),
                        placeholder = { defaultPlaceHolder("Filter logs...") },
                        containerColor = FloconTheme.colorPalette.secondary,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                addIncludeFilter()
                            }
                        ),
                        leadingComponent = {
                            FloconIcon(
                                imageVector = Icons.Outlined.Search,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                    )

                    androidx.compose.material3.IconButton(onClick = addIncludeFilter) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Include")
                    }
                    androidx.compose.material3.IconButton(onClick = addExcludeFilter) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Exclude")
                    }
                }

                FilterChip(
                    selected = !showTransactions,
                    onClick = {
                        toggleShowTransactions()
                    },
                    label = {
                        Text("Hide transactions", style = FloconTheme.typography.bodySmall)
                    },
                    leadingIcon = {
                        if (!showTransactions) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                        }
                    }
                )

                FloconOverflow {
                    FloconDropdownMenuItem(
                        text = "Export CSV",
                        leadingIcon = Icons.Outlined.Upload,
                        onClick = { exportToCsv() }
                    )
                    FloconDropdownMenuItem(
                        text = "Export Markdown",
                        leadingIcon = Icons.Outlined.UploadFile,
                        onClick = { exportToMarkdown() }
                    )
                }

            }
            FilterChips(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 8.dp),
                filterChips = filterChips,
                toggleFilterType = toggleFilterType,
                removeFilterChip = removeFilterChip,
            )
        }
    }
}

@Composable
private fun FilterChips(
    modifier: Modifier = Modifier,
    filterChips: List<FilterChipUiModel>,
    toggleFilterType: (FilterChipUiModel) -> Unit,
    removeFilterChip: (FilterChipUiModel) -> Unit
) {
    val itemsColor = FloconTheme.colorPalette.onSecondary
    if (filterChips.isNotEmpty()) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier,
        ) {
            filterChips.fastForEach { chip ->
                InputChip(
                    selected = true,
                    onClick = { toggleFilterType(chip) },
                    shape = RoundedCornerShape(20.dp),
                    label = {
                        Text(
                            chip.text,
                            style = FloconTheme.typography.bodySmall,
                            color = itemsColor,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (chip.type == FilterChipUiModel.FilterType.INCLUDE) Icons.Default.Add else Icons.Default.Remove,
                            contentDescription = null,
                            tint = itemsColor,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            tint = itemsColor,
                            contentDescription = "Remove",
                            modifier = Modifier.clip(CircleShape).size(14.dp)
                                .clickable {
                                    removeFilterChip(chip)
                                }
                        )
                    }
                )
            }
        }
    }
}
