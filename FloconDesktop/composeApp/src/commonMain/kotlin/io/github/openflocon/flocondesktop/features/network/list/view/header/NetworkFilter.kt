package io.github.openflocon.flocondesktop.features.network.list.view.header

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.library.designsystem.FloconTheme
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
                modifier = Modifier.weight(1f),
            )
            FloconIconButton(
                imageVector = Icons.Outlined.Delete,
                onClick = { onAction(NetworkAction.Reset) },
            )
            Box(
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.9f))
                    .clickable(
                        onClick = {
                            onAction(NetworkAction.OpenMocks)
                        },
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text = "Mocks",
                    style = FloconTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                    ),
                )
            }
            Box(
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.9f))
                    .clickable(
                        onClick = {
                            onAction(NetworkAction.OpenBadNetworkQuality)
                        },
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text = "Bad Network Quality",
                    style = FloconTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                    ),
                )
            }
        }
    }
}
