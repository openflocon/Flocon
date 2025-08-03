package io.github.openflocon.flocondesktop.features.network.ui.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun FilterDropdown(
    text: String,
    icon: Painter,
    content: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit
) {
    var filterExpanded by remember { mutableStateOf(false) }

    Column {
        SuggestionChip(
            onClick = { filterExpanded = !filterExpanded },
            label = { Text(text = text) },
            icon = {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            }
        )
        DropdownMenu(
            expanded = filterExpanded,
            onDismissRequest = { filterExpanded = false }
        ) {
            content({ filterExpanded = false })
        }
    }
}
