package io.github.openflocon.flocondesktop.features.table.view

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
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.tables_empty
import io.github.openflocon.flocondesktop.features.table.model.DeviceTableUiModel
import io.github.openflocon.flocondesktop.features.table.model.TablesStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.DropdownFilterFieldView
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator
import org.jetbrains.compose.resources.stringResource

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
                    TablesStateUiModel.Empty -> Text(stringResource(Res.string.tables_empty), style = FloconTheme.typography.bodySmall)
                    TablesStateUiModel.Loading -> FloconLinearProgressIndicator()
                    is TablesStateUiModel.WithContent -> {
                        Text(text = tablesState.selected.name, style = FloconTheme.typography.bodySmall)
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
            if (tablesState is TablesStateUiModel.WithContent) {
                val filteredItems = remember(filterText, tablesState.tables) {
                    tablesState.tables.filter {
                        it.name.contains(
                            filterText,
                            ignoreCase = true
                        )
                    }
                }
                filteredItems
                    .fastForEach { table ->
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
}
