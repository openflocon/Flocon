package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import io.github.openflocon.flocondesktop.features.database.model.DeviceDataBaseUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.DropdownFilterFieldView
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator

@Composable
internal fun DatabaseSelectorView(
    databasesState: DatabasesStateUiModel,
    onDatabaseSelected: (DeviceDataBaseUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onExpandRequest = {
            if (databasesState is DatabasesStateUiModel.WithContent)
                expanded = true
        },
        anchorContent = {
            FloconButton(
                onClick = {
                    if (databasesState is DatabasesStateUiModel.WithContent)
                        expanded = true
                },
                containerColor = FloconTheme.colorPalette.secondary
            ) {
                when (databasesState) {
                    DatabasesStateUiModel.Empty -> Text(
                        "No database",
                        style = FloconTheme.typography.bodySmall
                    )

                    DatabasesStateUiModel.Loading -> FloconLinearProgressIndicator()
                    is DatabasesStateUiModel.WithContent -> {
                        Text(
                            text = databasesState.selected.name,
                            style = FloconTheme.typography.bodySmall
                        )
                        FloconIcon(
                            imageVector = Icons.Outlined.KeyboardArrowDown,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) {
        var filterText by remember { mutableStateOf("") }
        DropdownFilterFieldView(
            value = filterText,
            onValueChanged = {
                filterText = it
            },
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            if (databasesState is DatabasesStateUiModel.WithContent) {
                val filteredItems = remember(filterText, databasesState.databases) {
                    databasesState.databases.filter {
                        it.name.contains(
                            filterText,
                            ignoreCase = true
                        )
                    }
                }

                filteredItems.fastForEach { database ->
                    FloconDropdownMenuItem(
                        text = database.name,
                        onClick = {
                            onDatabaseSelected(database)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
