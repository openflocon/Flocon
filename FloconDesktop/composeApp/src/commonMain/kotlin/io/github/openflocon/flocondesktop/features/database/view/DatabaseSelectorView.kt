package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import io.github.openflocon.flocondesktop.features.database.model.DeviceDataBaseUiModel
import io.github.openflocon.library.designsystem.FloconTheme
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
                    DatabasesStateUiModel.Empty -> Text("No database")
                    DatabasesStateUiModel.Loading -> FloconLinearProgressIndicator()
                    is DatabasesStateUiModel.WithContent -> {
                        Text(text = databasesState.selected.name)
                        FloconIcon(
                            imageVector = Icons.Outlined.KeyboardArrowDown
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) {
        if (databasesState is DatabasesStateUiModel.WithContent) {
            databasesState.databases
                .forEach { database ->
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
