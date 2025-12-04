package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowUiModel
import org.jetbrains.compose.resources.stringResource

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

    FilterBar(
        placeholderText = stringResource(Res.string.filter),
        modifier = modifier,
        onTextChange = {
            filterText = it
        },
    )
}
