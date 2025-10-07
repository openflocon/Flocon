package io.github.openflocon.flocondesktop.features.analytics.view

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
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsRowUiModel
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.library.designsystem.components.FloconIconButton
import androidx.compose.runtime.State

@Composable
fun AnalyticsFilterBar(
    filterText: State<String>,
    onFilterTextChanged: (String) -> Unit,
    onResetClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterBar(
            filterText = filterText,
            placeholderText = "Filter",
            modifier = Modifier.weight(1f),
            onTextChange = {
                onFilterTextChanged(it)
            },
        )
        FloconIconButton(
            imageVector = Icons.Outlined.Delete,
            onClick = {
                onResetClicked()
            },
        )
    }
}
