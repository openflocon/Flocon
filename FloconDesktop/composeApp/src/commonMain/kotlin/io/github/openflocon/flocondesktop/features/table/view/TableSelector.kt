package io.github.openflocon.flocondesktop.features.table.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEach
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.tables_empty
import flocondesktop.composeapp.generated.resources.tables_selector_caption
import io.github.openflocon.flocondesktop.features.table.model.DeviceTableUiModel
import io.github.openflocon.flocondesktop.features.table.model.TablesStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.DropdownFilterField
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconPageSelector
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TableSelector(
    tablesState: TablesStateUiModel,
    onTableSelected: (DeviceTableUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onExpandRequest = {
            if (tablesState is TablesStateUiModel.WithContent) {
                expanded = true
            }
        },
        anchorContent = {
            val onClick = {
                if (tablesState is TablesStateUiModel.WithContent) {
                    expanded = true
                }
            }
            when (tablesState) {
                TablesStateUiModel.Empty -> Unit

                TablesStateUiModel.Loading ->
                    FloconButton(onClick = onClick, containerColor = FloconTheme.colors.bg200) {
                        FloconLinearProgressIndicator()
                    }

                is TablesStateUiModel.WithContent -> FloconPageSelector(
                    icon = Icons.Outlined.TableChart,
                    caption = stringResource(Res.string.tables_selector_caption),
                    value = tablesState.selected.name,
                    onClick = onClick,
                )
            }
        },
        modifier = modifier
    ) {
        var filterText by remember { mutableStateOf("") }
        DropdownFilterField(
            value = filterText,
            onValueChanged = {
                filterText = it
            },
            modifier = Modifier.padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny)
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
