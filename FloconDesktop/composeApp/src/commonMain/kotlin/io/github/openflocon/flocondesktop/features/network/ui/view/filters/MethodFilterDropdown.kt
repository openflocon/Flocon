package io.github.openflocon.flocondesktop.features.network.ui.view.filters

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.MethodFilterState
import io.github.openflocon.flocondesktop.features.network.ui.view.components.MethodView

@Composable
fun MethodFilterDropdown(
    filterMethodExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onItemClicked: (NetworkMethodUi) -> Unit,
    filterState: MethodFilterState,
) {
    DropdownMenu(
        expanded = filterMethodExpanded,
        onDismissRequest = onDismissRequest
    ) {
        filterState.items.fastForEach { item ->
            val alpha by animateFloatAsState(if (item.isSelected) 1f else 0.3f)
            Box(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
                MethodView(
                    method = item.method,
                    modifier = Modifier.alpha(alpha),
                    onClick = {
                        onItemClicked(item.method)
                    }
                )
            }
        }
    }
}
