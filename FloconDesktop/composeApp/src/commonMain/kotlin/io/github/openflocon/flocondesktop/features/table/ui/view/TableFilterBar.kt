package io.github.openflocon.flocondesktop.features.table.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.ui.view.components.FilterBar
import io.github.openflocon.flocondesktop.features.table.ui.model.TableRowUiModel

@Composable
fun TableFilterBar(
    tableItems: List<TableRowUiModel>,
    onItemsChange: (List<TableRowUiModel>) -> Unit,
    onResetClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var filterText by remember {
        mutableStateOf("")
    }
    val onItemsChangeCallback by rememberUpdatedState(onItemsChange)
    val filteredTableItems: List<TableRowUiModel> =
        remember(tableItems, filterText) {
            if (filterText.isBlank()) {
                tableItems
            } else {
                tableItems.filter {
                    it.contains(filterText)
                }
            }
        }

    LaunchedEffect(filteredTableItems) {
        onItemsChangeCallback(filteredTableItems)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterBar(
            placeholderText = "Filter",
            modifier = Modifier.weight(1f),
            onTextChange = {
                filterText = it
            },
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = onResetClicked)
                .padding(all = 8.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
