package io.github.openflocon.flocondesktop.features.table.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.table.model.DeviceTableUiModel
import io.github.openflocon.flocondesktop.features.table.model.TablesStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator

@Composable
internal fun TableSelectorView(
    tablesState: TablesStateUiModel,
    onTableSelected: (DeviceTableUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onExpandRequest = {
            if (tablesState is TablesStateUiModel.WithContent)
                expanded = true
        },
        anchorContent = {
            FloconButton(
                onClick = {
                    if (tablesState is TablesStateUiModel.WithContent)
                        expanded = true
                },
                containerColor = FloconTheme.colorPalette.secondary
            ) {
                when (tablesState) {
                    TablesStateUiModel.Empty -> Text("No tables")
                    TablesStateUiModel.Loading -> FloconLinearProgressIndicator()
                    is TablesStateUiModel.WithContent -> {
                        Text(text = tablesState.selected.name)
                        FloconIcon(
                            imageVector = Icons.Outlined.KeyboardArrowDown
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) {
        if (tablesState is TablesStateUiModel.WithContent) {
            tablesState.tables
                .forEach { table ->
                    FloconDropdownMenuItem(
                        text = table.name,
                        onClick = {
                            onTableSelected(table)
                            expanded = false
                        }
                    )
                }
        }
    }
}
