package io.github.openflocon.flocondesktop.features.network.list.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.flocondesktop.common.ui.ContextualView
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemColumnWidths
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkStatusUi
import io.github.openflocon.flocondesktop.features.network.list.model.header.OnFilterAction
import io.github.openflocon.flocondesktop.features.network.list.model.header.TextFilterAction
import io.github.openflocon.flocondesktop.features.network.list.model.previewNetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.previewNetworkItemViewStateError
import io.github.openflocon.flocondesktop.features.network.list.view.components.MethodView
import io.github.openflocon.flocondesktop.features.network.list.view.components.StatusView
import io.github.openflocon.flocondesktop.features.network.list.view.components.errorTagText
import io.github.openflocon.flocondesktop.features.network.list.view.components.exceptionTagText
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import io.github.openflocon.library.designsystem.common.buildMenu
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.ui.tooling.preview.Preview

private val replayColor = Color(0xFF242D44)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NetworkItemView(
    state: NetworkItemViewState,
    multiSelect: Boolean,
    multiSelected: Boolean,
    selected: Boolean,
    onAction: (NetworkAction) -> Unit,
    modifier: Modifier = Modifier,
    columnWidths: NetworkItemColumnWidths = NetworkItemColumnWidths(), // Default widths provided
) {
    val bodySmall = FloconTheme.typography.bodySmall.copy(fontSize = 11.sp)

    val errorColor = remember(state) {
        when (state.status.status) {
            NetworkStatusUi.Status.ERROR -> errorTagText
            NetworkStatusUi.Status.EXCEPTION -> exceptionTagText
            else -> null
        }
    }

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
                    if (state.isReplayed) {
                        Modifier.background(replayColor)
                    } else if (state.isMocked) {
                        Modifier.background(FloconTheme.colorPalette.accent)
                    } else Modifier,
                )
                .combinedClickable(
                    onClick = { onAction(NetworkAction.SelectRequest(state.uuid)) },
                    onDoubleClick = { onAction(NetworkAction.DoubleClicked(state)) }
                )
                .then(
                    if (selected) {
                        Modifier.border(
                            width = 1.dp,
                            color = FloconTheme.colorPalette.accent,
                            shape = FloconTheme.shapes.medium
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 4.dp, vertical = 4.dp),
            // Inner padding for content
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AnimatedVisibility(multiSelect) {
                FloconCheckbox(
                    checked = multiSelected,
                    onCheckedChange = { onAction(NetworkAction.SelectLine(state.uuid, it)) },
                    uncheckedColor = FloconTheme.colorPalette.secondary
                )
            }
            // Date - Fixed width from data class
            Box(
                modifier = Modifier.width(columnWidths.dateWidth),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    state.dateFormatted,
                    style = bodySmall,
                    color = errorColor ?: FloconTheme.colorPalette.onPrimary
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
                    color = errorColor ?: FloconTheme.colorPalette.onPrimary,
                    modifier = Modifier
                        .weight(columnWidths.domainWeight)
                        .padding(horizontal = 4.dp),
                )
                Row(
                    modifier = Modifier
                        .weight(2f)
                        .background(
                            color = FloconTheme.colorPalette.secondary,
                            shape = RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    state.type.image()?.let {
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(state.type.imageColor())
                                .padding(3.dp)
                        ) {
                            Image(
                                imageVector = it,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                    }
                    Text(
                        text = state.type.query,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = bodySmall,
                        color = FloconTheme.colorPalette.onSecondary,
                    )
                }
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
            if (state.type !is NetworkItemViewState.NetworkTypeUi.Grpc && state.type !is NetworkItemViewState.NetworkTypeUi.WebSocket) {
                item(
                    label = "Copy cUrl",
                    onClick = { onActionCallback(NetworkAction.CopyCUrl(state)) }
                )
                item(
                    label = "Create Mock",
                    onClick = { onActionCallback(NetworkAction.CreateMock(state)) }
                )
                item(
                    label = "Replay",
                    onClick = { onActionCallback(NetworkAction.Replay(state)) }
                )
            }
            item(label = "Select Item", onClick = { onActionCallback(NetworkAction.SelectLine(state.uuid, selected = true)) })
            separator()
            subMenu(label = "Filter") {
                subMenu(label = "Include") {
                    item(label = "Domain", onClick = {
                        onActionCallback(
                            NetworkAction.HeaderAction.FilterAction(
                                OnFilterAction.TextFilter(
                                    column = NetworkTextFilterColumns.Domain,
                                    action = TextFilterAction.Include(text = state.domain)
                                )
                            )
                        )
                    })
                    item(label = "Query", onClick = {
                        onActionCallback(
                            NetworkAction.HeaderAction.FilterAction(
                                OnFilterAction.TextFilter(
                                    column = NetworkTextFilterColumns.Query,
                                    action = TextFilterAction.Include(text = state.type.query)
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
                                    action = TextFilterAction.Exclude(text = state.domain)
                                )
                            )
                        )
                    })
                    item(label = "Query", onClick = {
                        onActionCallback(
                            NetworkAction.HeaderAction.FilterAction(
                                OnFilterAction.TextFilter(
                                    column = NetworkTextFilterColumns.Query,
                                    action = TextFilterAction.Exclude(text = state.type.query)
                                )
                            )
                        )
                    })
                }
            }
            separator()
            item(label = "Remove", onClick = { onActionCallback(NetworkAction.Remove(state)) })
            item(
                label = "Remove lines above",
                onClick = { onActionCallback(NetworkAction.RemoveLinesAbove(state)) }
            )
            item(
                label = "Clear old sessions",
                onClick = { onActionCallback(NetworkAction.ClearOldSession) }
            )
        }
    }
}

private val NetworkItemViewState.NetworkTypeUi.query: String
    get() = when (this) {
        is NetworkItemViewState.NetworkTypeUi.GraphQl -> queryName
        is NetworkItemViewState.NetworkTypeUi.Grpc -> method
        is NetworkItemViewState.NetworkTypeUi.Url -> query
        is NetworkItemViewState.NetworkTypeUi.WebSocket -> text
    }

fun NetworkItemViewState.NetworkTypeUi.image(): ImageVector? = when (this) {
    is NetworkItemViewState.NetworkTypeUi.GraphQl,
    is NetworkItemViewState.NetworkTypeUi.Grpc,
    is NetworkItemViewState.NetworkTypeUi.Url -> null

    is NetworkItemViewState.NetworkTypeUi.WebSocket ->
        when (this.icon) {
            NetworkItemViewState.NetworkTypeUi.WebSocket.IconType.Up -> Icons.Default.Upload
            NetworkItemViewState.NetworkTypeUi.WebSocket.IconType.Down -> Icons.Default.Download
            null -> null
        }
}

fun NetworkItemViewState.NetworkTypeUi.imageColor(): Color = when (this) {
    is NetworkItemViewState.NetworkTypeUi.GraphQl,
    is NetworkItemViewState.NetworkTypeUi.Grpc,
    is NetworkItemViewState.NetworkTypeUi.Url -> null

    is NetworkItemViewState.NetworkTypeUi.WebSocket ->
        when (this.icon) {
            NetworkItemViewState.NetworkTypeUi.WebSocket.IconType.Up -> Color(0xFF007BFF)
            NetworkItemViewState.NetworkTypeUi.WebSocket.IconType.Down -> Color(0xFF28A745)
            null -> null
        }
} ?: Color.Green

@Composable
@Preview
private fun ItemViewPreview() {
    FloconTheme {
        NetworkItemView(
            modifier = Modifier.fillMaxWidth(),
            selected = false,
            multiSelect = false,
            multiSelected = false,
            state = previewNetworkItemViewState(),
            onAction = {},
        )
    }
}

@Composable
@Preview
private fun ItemViewPreview_error() {
    FloconTheme {
        FloconSurface {
            NetworkItemView(
                modifier = Modifier.fillMaxWidth(),
                selected = false,
                multiSelect = true,
                multiSelected = false,
                state = previewNetworkItemViewStateError(),
                onAction = {},
            )
        }
    }
}
