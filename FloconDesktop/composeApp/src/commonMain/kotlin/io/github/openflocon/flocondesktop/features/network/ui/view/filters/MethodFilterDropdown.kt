package io.github.openflocon.flocondesktop.features.network.ui.view.filters

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.previewMethodFilterState
import io.github.openflocon.flocondesktop.features.network.ui.view.components.MethodView
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MethodFilterDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onItemClicked: (NetworkMethodUi) -> Unit,
    filterState: MethodFilterState,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        MethodFilterDropdownContent(filterState = filterState, onItemClicked = onItemClicked)
    }
}

@Composable
private fun MethodFilterDropdownContent(
    filterState: MethodFilterState,
    onItemClicked: (NetworkMethodUi) -> Unit,
) {
    Column {
        filterState.items.fastForEach { item ->
            val alpha by animateFloatAsState(if (item.isSelected) 1f else 0.3f)
            Box(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
                MethodView(
                    method = item.method,
                    modifier = Modifier.alpha(alpha),
                    onClick = {
                        onItemClicked(item.method)
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun MethodFilterDropdownContentPreview() {
    FloconTheme {
        MethodFilterDropdownContent(
            filterState = previewMethodFilterState(),
            onItemClicked = {},
        )
    }
}
