package io.github.openflocon.flocondesktop.features.database.view.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.export_csv
import flocondesktop.composeapp.generated.resources.filter_exclude
import flocondesktop.composeapp.generated.resources.filter_include
import flocondesktop.composeapp.generated.resources.general_clear
import flocondesktop.composeapp.generated.resources.general_export_markdown
import flocondesktop.composeapp.generated.resources.general_hide_transactions
import flocondesktop.composeapp.generated.resources.general_next_page
import flocondesktop.composeapp.generated.resources.general_previous_page
import flocondesktop.composeapp.generated.resources.remove
import flocondesktop.composeapp.generated.resources.settings_filter_logs
import io.github.openflocon.flocondesktop.common.ui.ContextualMenu
import io.github.openflocon.flocondesktop.features.database.DatabaseQueryLogsViewModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryUiModel
import io.github.openflocon.flocondesktop.features.database.model.FilterChipUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconDropdownSeparator
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconOverflow
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DatabaseQueryLogs(
    dbName: String,
    modifier: Modifier = Modifier
) {
    val viewModel: DatabaseQueryLogsViewModel = koinViewModel(
        key = dbName,
        parameters = { parametersOf(dbName) }
    )

    val logs: List<DatabaseQueryUiModel> by viewModel.logs.collectAsStateWithLifecycle()
    val showTransactions by viewModel.showTransactions.collectAsStateWithLifecycle()
    val filterChips by viewModel.filterChips.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val paginationLabel by viewModel.paginationLabel.collectAsStateWithLifecycle()
    val totalPages by viewModel.totalPages.collectAsStateWithLifecycle()
    val page by viewModel.page.collectAsStateWithLifecycle()

    val lazyListState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(lazyListState)

    Column(modifier = modifier) {
        DatabaseLogsHeader(
            modifier = Modifier.fillMaxWidth().border(
                width = 1.dp,
                color = FloconTheme.colors.bg200,
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
            clearLogs = viewModel::clearLogs,
            paginationLabel = paginationLabel,
            onNextPage = viewModel::nextPage,
            onPreviousPage = viewModel::previousPage,
            isNextEnabled = page < totalPages - 1,
            isPreviousEnabled = page > 0,
        )

        Box(
            modifier = Modifier.fillMaxSize().padding(top = FloconTheme.spacing.medium)
                .clip(FloconTheme.shapes.medium)
                .border(
                    width = 1.dp,
                    color = FloconTheme.colors.bg200,
                    shape = FloconTheme.shapes.medium
                )
                .background(
                    color = FloconTheme.colors.bg100,
                    shape = FloconTheme.shapes.medium
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
            ) {
                items(logs) { log ->
                    ContextualMenu(
                        modifier = Modifier
                            .fillMaxWidth(),
                        items = persistentListOf(
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
                        DatabaseQueryLogItem(
                            log = log,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.small)
                        )
                    }
                    HorizontalDivider(color = FloconTheme.colors.bg200)
                }
            }
            FloconVerticalScrollbar(
                adapter = scrollAdapter,
                modifier = Modifier.fillMaxHeight()
                    .align(Alignment.TopEnd),
            )
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
    clearLogs: () -> Unit,
    paginationLabel: String,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
    isNextEnabled: Boolean,
    isPreviousEnabled: Boolean,
    filterChips: ImmutableList<FilterChipUiModel>,
    modifier: Modifier = Modifier,
) {
    FloconPageTopBar(modifier) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
                ) {
                    FloconTextFieldWithoutM3(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        modifier = Modifier.weight(1f),
                        placeholder = { defaultPlaceHolder(stringResource(Res.string.settings_filter_logs)) },
                        containerColor = FloconTheme.colors.bg200,
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

                    IconButton(onClick = addIncludeFilter) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(Res.string.filter_include))
                    }
                    IconButton(onClick = addExcludeFilter) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = stringResource(Res.string.filter_exclude))
                    }
                }

                FilterChip(
                    selected = !showTransactions,
                    onClick = {
                        toggleShowTransactions()
                    },
                    label = {
                        Text(stringResource(Res.string.general_hide_transactions), style = FloconTheme.typography.bodySmall)
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

                PageSelector(
                    onPreviousPage = onPreviousPage,
                    isPreviousEnabled = isPreviousEnabled,
                    paginationLabel = paginationLabel,
                    onNextPage = onNextPage,
                    isNextEnabled = isNextEnabled,
                    modifier = Modifier.padding(start = FloconTheme.spacing.large),
                )

                FloconOverflow {
                    FloconDropdownMenuItem(
                        text = stringResource(Res.string.export_csv),
                        leadingIcon = Icons.Outlined.Upload,
                        onClick = { exportToCsv() }
                    )
                    FloconDropdownMenuItem(
                        text = stringResource(Res.string.general_export_markdown),
                        leadingIcon = Icons.Outlined.UploadFile,
                        onClick = { exportToMarkdown() }
                    )
                    FloconDropdownSeparator()
                    FloconDropdownMenuItem(
                        text = stringResource(Res.string.general_clear),
                        leadingIcon = Icons.Outlined.Delete,
                        onClick = { clearLogs() }
                    )
                }
            }
            FilterChips(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = FloconTheme.spacing.small),
                filterChips = filterChips,
                toggleFilterType = toggleFilterType,
                removeFilterChip = removeFilterChip,
            )
        }
    }
}

@Composable
private fun PageSelector(
    onPreviousPage: () -> Unit,
    isPreviousEnabled: Boolean,
    paginationLabel: String,
    onNextPage: () -> Unit,
    isNextEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier,
    ) {
        IconButton(
            onClick = onPreviousPage,
            enabled = isPreviousEnabled
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = stringResource(Res.string.general_previous_page),
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = paginationLabel,
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.ink
        )

        IconButton(
            onClick = onNextPage,
            enabled = isNextEnabled
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = stringResource(Res.string.general_next_page),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun FilterChips(
    filterChips: ImmutableList<FilterChipUiModel>,
    toggleFilterType: (FilterChipUiModel) -> Unit,
    modifier: Modifier = Modifier,
    removeFilterChip: (FilterChipUiModel) -> Unit
) {
    val itemsColor = FloconTheme.colors.inkMuted
    if (filterChips.isNotEmpty()) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
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
                            contentDescription = stringResource(Res.string.remove),
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
