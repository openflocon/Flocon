package io.github.openflocon.flocondesktop.features.database.view.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import io.github.openflocon.flocondesktop.features.database.DatabaseQueryLogsViewModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DatabaseQueryLogsView(
    dbName: String,
    modifier: Modifier = Modifier
) {
    val viewModel: DatabaseQueryLogsViewModel = koinViewModel(
        parameters = { parametersOf(dbName) }
    )

    val logs = viewModel.logs.collectAsLazyPagingItems()
    val showTransactions by viewModel.showTransactions.collectAsStateWithLifecycle()
    val filterChips by viewModel.filterChips.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            androidx.compose.material3.OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Filter logs...") },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onDone = {
                        viewModel.addFilterChip()
                    }
                )
            )

            if (filterChips.isNotEmpty()) {
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filterChips.size) { index ->
                        val chip = filterChips[index]
                        androidx.compose.material3.InputChip(
                            selected = true,
                            onClick = { },
                            label = { Text(chip) },
                            trailingIcon = {
                                androidx.compose.material3.IconButton(
                                    onClick = { viewModel.removeFilterChip(chip) },
                                    modifier = Modifier.size(16.dp)
                                ) {
                                   Icon(
                                       imageVector = Icons.Default.Close,
                                       contentDescription = "Remove",
                                       modifier = Modifier.size(12.dp)
                                   )
                                }
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilterChip(
                    selected = !showTransactions,
                    onClick = {
                        viewModel.toggleShowTransactions()
                    },
                    label = {
                        Text("Hide transactions")
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
        }


        Box(
            modifier = Modifier.fillMaxSize()
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
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        HorizontalDivider(color = FloconTheme.colorPalette.secondary)
                    }
                }
            }
        }
    }
}
