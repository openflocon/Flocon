package io.github.openflocon.flocondesktop.features.network.ui.view.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.ui.NetworkAction
import io.github.openflocon.flocondesktop.features.network.ui.view.components.FilterBar
import io.github.openflocon.library.designsystem.components.FloconIconButton

@Composable
fun NetworkFilter(
    onAction: (NetworkAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterBar(
                placeholderText = "Filter route",
                onTextChange = { onAction(NetworkAction.FilterQuery(it)) },
                modifier = Modifier.weight(1f)
            )
            FloconIconButton(
                imageVector = Icons.Outlined.Delete,
                onClick = { onAction(NetworkAction.Reset) }
            )
        }
    }
}

