package com.florent37.flocondesktop.features.grpc.ui.view

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
import androidx.compose.ui.unit.dp
import com.florent37.flocondesktop.common.ui.ContextualItem
import com.florent37.flocondesktop.common.ui.ContextualView
import com.florent37.flocondesktop.features.grpc.ui.model.GrpcItemColumnWidths
import com.florent37.flocondesktop.features.grpc.ui.model.GrpcItemViewState
import com.florent37.flocondesktop.features.grpc.ui.model.OnGrpcItemUserAction
import com.florent37.flocondesktop.features.grpc.ui.model.previewGrpcItemViewState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GrpcItemView(
    state: GrpcItemViewState,
    columnWidths: GrpcItemColumnWidths = GrpcItemColumnWidths(), // Default widths provided
    onUserAction: (OnGrpcItemUserAction) -> Unit,
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
                id = "copy_method",
                text = "Copy Method",
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
                "copy_url" -> onUserAction(OnGrpcItemUserAction.CopyUrl(state))
                "copy_method" -> onUserAction(OnGrpcItemUserAction.CopyMethod(state))
                "remove" -> onUserAction(OnGrpcItemUserAction.Remove(state))
                "remove_lines_above" -> onUserAction(OnGrpcItemUserAction.RemoveLinesAbove(state))
            }
        },
    ) {
        Row(
            modifier = modifier
                .padding(horizontal = 8.dp, vertical = 4.dp) // Padding for the entire item
                .clip(shape = RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    onUserAction(OnGrpcItemUserAction.OnClicked(state))
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
                modifier = Modifier.width(columnWidths.requestTimeFormatted),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    state.requestTimeFormatted,
                    style = bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }

            // Request Url - Fixed width from data class
            Box(
                modifier = Modifier.weight(columnWidths.url),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    state.url,
                    style = bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }

            // Method - Takes remaining space (weight)
            Box(
                modifier = Modifier.weight(columnWidths.method),
            ) {
                Text(
                    state.method,
                    style = bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            // Status - Fixed width from data class

            // TODO add a badge here
            Box(
                modifier = Modifier.width(columnWidths.status),
                contentAlignment = Alignment.Center,
            ) {
                GrpcStatusView(
                    state.status,
                )
            }

            // Duration - Fixed width from data class
            Box(
                modifier = Modifier.width(columnWidths.durationFormatted),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    state.durationFormatted ?: "", // reserve this space
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
        GrpcItemView(
            modifier = Modifier.fillMaxWidth(),
            state = previewGrpcItemViewState(),
            onUserAction = {},
        )
    }
}
