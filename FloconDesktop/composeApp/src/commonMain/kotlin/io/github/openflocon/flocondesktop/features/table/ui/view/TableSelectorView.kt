package io.github.openflocon.flocondesktop.features.table.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.table.ui.model.DeviceTableUiModel
import io.github.openflocon.flocondesktop.features.table.ui.model.TablesStateUiModel
import io.github.openflocon.flocondesktop.features.table.ui.model.previewDeviceTableUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun TableSelectorView(
    tablesState: TablesStateUiModel,
    onTableSelected: (DeviceTableUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    val contentPadding =
        PaddingValues(
            horizontal = 8.dp,
            vertical = 4.dp,
        )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Table : ",
            color = FloconTheme.colorPalette.onBackground,
            style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        )

        Spacer(modifier = Modifier.width(4.dp))

        when (tablesState) {
            TablesStateUiModel.Loading -> {
                // hide
            }

            TablesStateUiModel.Empty -> {
                Text(
                    modifier = Modifier
                        .background(FloconTheme.colorPalette.onBackground, shape = shape)
                        .padding(contentPadding),
                    text = "No Table Found",
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colorPalette.background,
                )
            }

            is TablesStateUiModel.WithContent -> {
                var expanded by remember { mutableStateOf(false) }

                TableView(
                    table = tablesState.selected,
                    textColor = FloconTheme.colorPalette.background,
                    modifier =
                    Modifier
                        .clip(shape)
                        .background(FloconTheme.colorPalette.onBackground)
                        .clickable { expanded = true }
                        .padding(contentPadding),
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    tablesState.tables.forEach { table ->
                        DropdownMenuItem(
                            text = {
                                TableView(
                                    table = table,
                                    textColor = FloconTheme.colorPalette.onBackground,
                                    modifier = Modifier.padding(all = 4.dp),
                                )
                            },
                            onClick = {
                                onTableSelected(table)
                                expanded = false // Close the dropdown after selection
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TableView(
    table: DeviceTableUiModel,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = table.name,
        color = textColor,
        style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
    )
}

@Preview
@Composable
private fun TableViewPreview() {
    FloconTheme {
        TableView(
            table = previewDeviceTableUiModel(),
            textColor = FloconTheme.colorPalette.background,
        )
    }
}
