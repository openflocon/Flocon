package io.github.openflocon.flocondesktop.features.network.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.common.ui.ContextualItem
import io.github.openflocon.flocondesktop.common.ui.ContextualView
import io.github.openflocon.flocondesktop.common.ui.FloconColors
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkStatusUi
import io.github.openflocon.flocondesktop.features.network.ui.model.OnNetworkItemUserAction
import io.github.openflocon.flocondesktop.features.network.ui.model.previewNetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.view.components.MethodView
import io.github.openflocon.flocondesktop.features.network.ui.view.components.StatusView
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Data class to define the fixed widths for each column in NetworkItemView.
 * This allows for easy configuration and consistency across all items in a LazyColumn.
 */
data class NetworkItemColumnWidths(
    val dateWidth: Dp = 90.dp,
    val methodWidth: Dp = 70.dp,
    val statusCodeWidth: Dp = 60.dp,
    val requestSizeWidth: Dp = 65.dp,
    val responseSizeWidth: Dp = 65.dp,
    val timeWidth: Dp = 60.dp,
    // The 'route' column will use Modifier.weight(1f) to take remaining space
)

@Composable
fun NetworkItemView(
    state: NetworkItemViewState,
    columnWidths: NetworkItemColumnWidths = NetworkItemColumnWidths(), // Default widths provided
    onUserAction: (OnNetworkItemUserAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Use MaterialTheme.typography for consistent text sizes
    val bodySmall = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
    val labelSmall = MaterialTheme.typography.labelSmall // Even smaller, good for labels/tags

    ContextualView(
        listOf(
            ContextualItem(
                id = "copy_url",
                text = "Copy url",
            ),
            ContextualItem(
                id = "copy_curl",
                text = "Copy cUrl",
            ),
            ContextualItem(
                id = "remove",
                text = "Remove",
            ),
            ContextualItem(
                id = "remove_lines_above",
                text = "Remove lines above ",
            ),
        ),
        onSelect = {
            when (it.id) {
                "copy_url" -> onUserAction(OnNetworkItemUserAction.CopyUrl(state))
                "copy_curl" -> onUserAction(OnNetworkItemUserAction.CopyCUrl(state))
                "remove" -> onUserAction(OnNetworkItemUserAction.Remove(state))
                "remove_lines_above" -> onUserAction(OnNetworkItemUserAction.RemoveLinesAbove(state))
            }
        },
    ) {
        Row(
            modifier = modifier
                .padding(vertical = 4.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    onUserAction(OnNetworkItemUserAction.OnClicked(state))
                })
                .padding(horizontal = 8.dp, vertical = 6.dp),
            // Inner padding for content
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Date - Fixed width from data class
            Box(
                modifier = Modifier.width(columnWidths.dateWidth),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    state.dateFormatted,
                    style = bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }

            // Method - Fixed width for the tag from data class
            MethodView(
                method = state.method,
                modifier =
                    Modifier
                        .width(columnWidths.methodWidth),
            )

            // Route - Takes remaining space (weight)
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = state.domain,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                        .padding(horizontal = 4.dp),
                )
                when (val type = state.type) {
                    is NetworkItemViewState.NetworkTypeUi.GraphQl -> {
                        Text(
                            text = type.queryName,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(2f)
                                .background(color = FloconColors.pannel.copy(alpha = 0.8f), shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }

                    is NetworkItemViewState.NetworkTypeUi.Url -> {
                        Text(
                            text = type.query,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(2f)
                                .background(color = FloconColors.pannel.copy(alpha = 0.8f), shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                }
            }

            // NetworkStatusUi - Fixed width for the tag from data class
            StatusView(
                state.status,
                modifier = Modifier.width(columnWidths.statusCodeWidth), // Apply fixed width to the StatusView composable
            )

            // Time - Fixed width from data class
            Box(
                modifier = Modifier.width(columnWidths.timeWidth),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    state.timeFormatted,
                    style = bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
@Preview
private fun ItemViewPreview() {
    MaterialTheme {
        NetworkItemView(
            modifier = Modifier.fillMaxWidth(),
            state = previewNetworkItemViewState(),
            onUserAction = {},
        )
    }
}
