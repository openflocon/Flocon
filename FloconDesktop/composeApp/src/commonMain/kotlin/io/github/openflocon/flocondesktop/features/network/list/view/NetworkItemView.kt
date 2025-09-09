package io.github.openflocon.flocondesktop.features.network.list.view

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.flocondesktop.common.ui.ContextualView
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemColumnWidths
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.header.OnFilterAction
import io.github.openflocon.flocondesktop.features.network.list.model.header.TextFilterAction
import io.github.openflocon.flocondesktop.features.network.list.model.previewNetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.view.components.MethodView
import io.github.openflocon.flocondesktop.features.network.list.view.components.StatusView
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import io.github.openflocon.library.designsystem.common.buildMenu
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NetworkItemView(
    state: NetworkItemViewState,
    onAction: (NetworkAction) -> Unit,
    modifier: Modifier = Modifier,
    columnWidths: NetworkItemColumnWidths = NetworkItemColumnWidths(), // Default widths provided
) {
    val bodySmall = FloconTheme.typography.bodySmall.copy(fontSize = 11.sp)

    ContextualView(
        items = contextualActions(
            onAction = onAction,
            state = state,
        ),
    ) {
        Row(
            modifier = modifier
                .clip(shape = RoundedCornerShape(8.dp))
                .then(
                    if (state.isFromOldAppInstance) {
                        Modifier.alpha(0.4f)
                    } else Modifier
                )
                .then(
                    if (state.isMocked) {
                        Modifier.background(FloconTheme.colorPalette.accent)
                    } else Modifier,
                )
                .clickable(onClick = { onAction(NetworkAction.SelectRequest(state.uuid)) })
                .padding(horizontal = 4.dp, vertical = 4.dp),
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
                    color = FloconTheme.colorPalette.onPrimary
                )
            }

            // Method - Fixed width for the tag from data class
            MethodView(
                method = state.method,
                modifier = Modifier.width(columnWidths.methodWidth),
            )

            // Route - Takes remaining space (weight)
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = state.domain,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = bodySmall,
                    color = FloconTheme.colorPalette.onPrimary,
                    modifier = Modifier
                        .weight(columnWidths.domainWeight)
                        .padding(horizontal = 4.dp),
                )
                Text(
                    text = state.type.query,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = bodySmall,
                    color = FloconTheme.colorPalette.onSecondary,
                    modifier = Modifier
                        .weight(2f)
                        .background(
                            color = FloconTheme.colorPalette.secondary,
                            shape = RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }

            // NetworkStatusUi - Fixed width for the tag from data class
            StatusView(
                status = state.status,
                modifier = Modifier.width(columnWidths.statusCodeWidth), // Apply fixed width to the StatusView composable
            )

            // Time - Fixed width from data class
            Box(
                modifier = Modifier.width(columnWidths.timeWidth),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    state.timeFormatted ?: "",
                    style = bodySmall,
                    color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
private fun contextualActions(
    onAction: (NetworkAction) -> Unit,
    state: NetworkItemViewState
): List<FloconContextMenuItem> {
    val onActionCallback by rememberUpdatedState(onAction)
    return remember(state) {
        buildMenu {
            item(label = "Copy URL", onClick = { onActionCallback(NetworkAction.CopyUrl(state)) })
            if (state.type !is NetworkItemViewState.NetworkTypeUi.Grpc) {
                item(label = "Copy cUrl", onClick = { onActionCallback(NetworkAction.CopyCUrl(state)) })
                item(label = "Create Mock", onClick = { onActionCallback(NetworkAction.CreateMock(state)) })
            }
            separator()
            subMenu(label = "Filter") {
                subMenu(label = "Include") {
                    item(label = "Domain", onClick = {
                        onActionCallback(
                            NetworkAction.HeaderAction.FilterAction(
                                OnFilterAction.TextFilter(
                                    column = NetworkTextFilterColumns.Domain,
                                    action = TextFilterAction.Include(text = state.domain, isRegex = false)
                                )
                            )
                        )
                    })
                    item(label = "Query", onClick = {
                        onActionCallback(
                            NetworkAction.HeaderAction.FilterAction(
                                OnFilterAction.TextFilter(
                                    column = NetworkTextFilterColumns.Query,
                                    action = TextFilterAction.Include(text = state.type.query, isRegex = false)
                                )
                            )
                        )
                    })
                }
                subMenu("Exclude") {
                    item("Domain", onClick = {
                        onActionCallback(
                            NetworkAction.HeaderAction.FilterAction(
                                OnFilterAction.TextFilter(
                                    column = NetworkTextFilterColumns.Domain,
                                    action = TextFilterAction.Exclude(text = state.domain, isRegex = false)
                                )
                            )
                        )
                    })
                    item(label = "Query", onClick = {
                        onActionCallback(
                            NetworkAction.HeaderAction.FilterAction(
                                OnFilterAction.TextFilter(
                                    column = NetworkTextFilterColumns.Query,
                                    action = TextFilterAction.Exclude(text = state.type.query, isRegex = false)
                                )
                            )
                        )
                    })
                }
            }
            separator()
            item(label = "Remove", onClick = { onActionCallback(NetworkAction.Remove(state)) })
            item(label = "Remove lines above", onClick = { onActionCallback(NetworkAction.RemoveLinesAbove(state)) })
        }
    }
}

private val NetworkItemViewState.NetworkTypeUi.query: String
    get() = when (this) {
        is NetworkItemViewState.NetworkTypeUi.GraphQl -> queryName
        is NetworkItemViewState.NetworkTypeUi.Grpc -> method
        is NetworkItemViewState.NetworkTypeUi.Url -> query
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
