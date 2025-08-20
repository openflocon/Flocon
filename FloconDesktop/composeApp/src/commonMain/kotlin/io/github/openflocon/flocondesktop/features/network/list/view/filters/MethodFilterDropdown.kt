package io.github.openflocon.flocondesktop.features.network.list.view.filters

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.MethodFilterState
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.previewMethodFilterState
import io.github.openflocon.flocondesktop.features.network.list.view.components.MethodView
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MethodFilterDropdownContent(
    filterState: MethodFilterState,
    onItemClicked: (NetworkMethodUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
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
