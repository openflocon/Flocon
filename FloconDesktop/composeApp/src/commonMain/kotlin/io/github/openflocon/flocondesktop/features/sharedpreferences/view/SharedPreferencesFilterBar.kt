package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowUiModel
import io.github.openflocon.library.designsystem.components.FloconIconButton

@Composable
fun SharedPreferencesFilterBar(
    items: List<SharedPreferencesRowUiModel>,
    onItemsChange: (List<SharedPreferencesRowUiModel>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var filterText by remember {
        mutableStateOf("")
    }
    val onItemsChangeCallback by rememberUpdatedState(onItemsChange)
    val filteredAnalyticsItems: List<SharedPreferencesRowUiModel> =
        remember(items, filterText) {
            if (filterText.isBlank()) {
                items
            } else {
                items.filter {
                    it.contains(filterText)
                }
            }
        }

    LaunchedEffect(filteredAnalyticsItems) {
        onItemsChangeCallback(filteredAnalyticsItems)
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
    }
}
