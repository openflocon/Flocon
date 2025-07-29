package com.florent37.flocondesktop.features.network.ui.view

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.florent37.flocondesktop.common.ui.ContextualItem
import com.florent37.flocondesktop.common.ui.ContextualView
import com.florent37.flocondesktop.features.network.ui.model.NetworkItemViewState
import com.florent37.flocondesktop.features.network.ui.model.OnNetworkItemUserAction
import com.florent37.flocondesktop.features.network.ui.model.previewNetworkItemViewState
import com.florent37.flocondesktop.features.network.ui.view.components.MethodView
import com.florent37.flocondesktop.features.network.ui.view.components.StatusView
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Data class to define the fixed widths for each column in NetworkItemView.
 * This allows for easy configuration and consistency across all items in a LazyColumn.
 */
data class NetworkItemColumnWidths(
    val dateWidth: Dp = 90.dp,
    val methodWidth: Dp = 65.dp,
    val statusCodeWidth: Dp = 50.dp,
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
    val bodySmall = MaterialTheme.typography.bodySmall // Typically 12.sp or similar
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
                .padding(horizontal = 8.dp, vertical = 4.dp) // Padding for the entire item
                .clip(shape = RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    onUserAction(OnNetworkItemUserAction.OnClicked(state))
                })
                .background(
                    color = MaterialTheme.colorScheme.surface, // Use surface color for the item background
                )
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
            Box(modifier = Modifier.weight(1f)) {
                Text(
                    state.route,
                    style = bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            // NetworkStatusUi - Fixed width for the tag from data class
            StatusView(
                state.networkStatusUi,
                modifier = Modifier.width(columnWidths.statusCodeWidth), // Apply fixed width to the StatusView composable
            )

            // Request Size - Fixed width from data class
            Box(
                modifier = Modifier.width(columnWidths.requestSizeWidth),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    state.requestSize,
                    style = bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }

            // Response Size - Fixed width from data class
            Box(
                modifier = Modifier.width(columnWidths.responseSizeWidth),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    state.responseSize,
                    style = bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }

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
