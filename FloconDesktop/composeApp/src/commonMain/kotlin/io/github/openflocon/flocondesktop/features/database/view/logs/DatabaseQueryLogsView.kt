package io.github.openflocon.flocondesktop.features.database.view.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import io.github.openflocon.flocondesktop.features.database.DatabaseQueryLogsViewModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
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
            addFilterChip = viewModel::addFilterChip,
            showTransactions = showTransactions,
            filterChips = filterChips
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
                        DatabaseQueryLogItemView(
                            log = log,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        )
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
    addFilterChip: () -> Unit,
    toggleShowTransactions: () -> Unit,
    removeFilterChip: (String) -> Unit,
    filterChips: List<String>,
    modifier: Modifier = Modifier,
) {
    FloconPageTopBar(modifier) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
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
                            addFilterChip()
                        }
                    ),
                    leadingComponent = {
                        FloconIcon(
                            imageVector = Icons.Outlined.Search,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                )

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

            }
            if (filterChips.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    items(filterChips.size) { index ->
                        val chip = filterChips[index]
                        InputChip(
                            selected = true,
                            onClick = { },
                            shape = RoundedCornerShape(20.dp),
                            label = { Text(chip, style = FloconTheme.typography.bodySmall) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
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
    }
}
