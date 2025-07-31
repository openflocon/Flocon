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
import androidx.compose.material3.MaterialTheme
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
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.features.table.ui.model.DeviceTableUiModel
import com.florent37.flocondesktop.features.table.ui.model.TablesStateUiModel
import com.florent37.flocondesktop.features.table.ui.model.previewDeviceTableUiModel
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
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        )

        Spacer(modifier = Modifier.width(4.dp))

        when (tablesState) {
            TablesStateUiModel.Loading -> {
                // hide
            }

            TablesStateUiModel.Empty -> {
                Text(
                    modifier =
                    Modifier
                        .background(MaterialTheme.colorScheme.onBackground, shape = shape)
                        .padding(contentPadding),
                    text = "No Table Found",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.background,
                )
            }

            is TablesStateUiModel.WithContent -> {
                var expanded by remember { mutableStateOf(false) }

                TableView(
                    table = tablesState.selected,
                    textColor = MaterialTheme.colorScheme.background,
                    modifier =
                    Modifier
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.onBackground)
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
                                    textColor = MaterialTheme.colorScheme.onBackground,
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
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
    )
}

@Preview
@Composable
private fun TableViewPreview() {
    FloconTheme {
        TableView(
            table = previewDeviceTableUiModel(),
            textColor = MaterialTheme.colorScheme.background,
        )
    }
}
