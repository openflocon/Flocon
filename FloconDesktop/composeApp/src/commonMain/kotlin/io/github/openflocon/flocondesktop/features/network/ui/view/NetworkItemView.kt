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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.common.ui.ContextualItem
import io.github.openflocon.flocondesktop.common.ui.ContextualView
import io.github.openflocon.flocondesktop.features.network.ui.NetworkAction
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.previewNetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.view.components.MethodView
import io.github.openflocon.flocondesktop.features.network.ui.view.components.StatusView
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NetworkItemView(
    state: NetworkItemViewState,
    onAction: (NetworkAction) -> Unit,
    modifier: Modifier = Modifier,
    columnWidths: NetworkItemColumnWidths = NetworkItemColumnWidths(), // Default widths provided
) {
    // Use FloconTheme.typography for consistent text sizes
    val bodySmall = FloconTheme.typography.bodySmall.copy(fontSize = 11.sp)
    FloconTheme.typography.labelSmall // Even smaller, good for labels/tags

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
                "copy_url" -> onAction(NetworkAction.CopyUrl(state))
                "copy_curl" -> onAction(NetworkAction.CopyCUrl(state))
                "remove" -> onAction(NetworkAction.Remove(state))
                "remove_lines_above" -> onAction(NetworkAction.RemoveLinesAbove(state))
            }
        },
    ) {
        Row(
            modifier = modifier
                .padding(vertical = 4.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .clickable(onClick = { onAction(NetworkAction.SelectRequest(state.uuid)) })
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
                    color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.7f),
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
                    color = FloconTheme.colorPalette.onSurface,
                    modifier = Modifier.weight(columnWidths.domainWeight)
                        .padding(horizontal = 4.dp),
                )
                when (val type = state.type) {
                    is NetworkItemViewState.NetworkTypeUi.GraphQl -> {
                        Text(
                            text = type.queryName,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = bodySmall,
                            color = FloconTheme.colorPalette.onSurface,
                            modifier = Modifier.weight(2f)
                                .background(
                                    color = FloconTheme.colorPalette.panel.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(4.dp),
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                        )
                    }

                    is NetworkItemViewState.NetworkTypeUi.Url -> {
                        Text(
                            text = type.query,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = bodySmall,
                            color = FloconTheme.colorPalette.onSurface,
                            modifier = Modifier.weight(2f)
                                .background(
                                    color = FloconTheme.colorPalette.panel.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(4.dp),
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                        )
                    }

                    is NetworkItemViewState.NetworkTypeUi.Grpc -> {
                        Text(
                            text = type.method,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = bodySmall,
                            color = FloconTheme.colorPalette.onSurface,
                            modifier = Modifier.weight(2f)
                                .background(
                                    color = FloconTheme.colorPalette.panel.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(4.dp),
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp),
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
                    color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.7f),
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
            onAction = {},
        )
    }
}
