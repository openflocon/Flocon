package io.github.openflocon.flocondesktop.features.network.list.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import io.github.openflocon.library.designsystem.common.buildMenu
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.floconMonoStyle
import kotlinx.collections.immutable.ImmutableList
import androidx.compose.desktop.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.clear_old_sessions
import flocondesktop.composeapp.generated.resources.filter
import flocondesktop.composeapp.generated.resources.filter_exclude
import flocondesktop.composeapp.generated.resources.filter_include
import flocondesktop.composeapp.generated.resources.network_copy_curl
import flocondesktop.composeapp.generated.resources.network_copy_url
import flocondesktop.composeapp.generated.resources.network_create_mock
import flocondesktop.composeapp.generated.resources.network_header_domain
import flocondesktop.composeapp.generated.resources.network_header_query
import flocondesktop.composeapp.generated.resources.network_replay
import flocondesktop.composeapp.generated.resources.network_select_item
import flocondesktop.composeapp.generated.resources.network_share_markdown
import flocondesktop.composeapp.generated.resources.remove
import flocondesktop.composeapp.generated.resources.remove_lines_above

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
    val colors = FloconTheme.colors
    val bodySmall = FloconTheme.typography.bodySmall
    val mono = floconMonoStyle()
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()

    val errorColor = when (state.status.status) {
        NetworkStatusUi.Status.ERROR -> colors.statusError
        NetworkStatusUi.Status.EXCEPTION -> colors.catCrash
        else -> null
    }

    ContextualView(
        items = contextualActions(
            onAction = onAction,
            state = state,
        ),
    ) {
        Row(
            modifier = modifier
                .then(
                    if (state.isFromOldAppInstance) {
                        Modifier.alpha(0.4f)
                    } else Modifier
                )
                .background(
                    when {
                        selected || hovered -> colors.bg300
                        state.isReplayed -> colors.catNetworkSoft
                        state.isMocked -> colors.accentSoft
                        else -> Color.Transparent
                    }
                )
                .hoverable(interactionSource)
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { onAction(NetworkAction.SelectRequest(state.uuid)) },
                    onDoubleClick = { onAction(NetworkAction.DoubleClicked(state)) }
                )
                .drawBehind {
                    // the separator of the rows, and the bar of the selected one
                    drawLine(
                        color = colors.line,
                        start = Offset(0f, size.height - 0.5.dp.toPx()),
                        end = Offset(size.width, size.height - 0.5.dp.toPx()),
                        strokeWidth = 1.dp.toPx(),
                    )
                    if (selected) {
                        drawRect(color = colors.accent, size = Size(2.dp.toPx(), size.height))
                    }
                }
                .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
            // Inner padding for content
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            AnimatedVisibility(multiSelect) {
                FloconCheckbox(
                    checked = multiSelected,
                    onCheckedChange = { onAction(NetworkAction.SelectLine(state.uuid, it)) },
                    uncheckedColor = FloconTheme.colors.bg200
                )
            }
            // Date - Fixed width from data class
            Box(
                modifier = Modifier.width(columnWidths.dateWidth),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    state.dateFormatted,
                    style = mono,
                    fontWeight = FontWeight.Medium,
                    color = errorColor ?: colors.ink
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
                    color = errorColor ?: colors.inkMuted,
                    modifier = Modifier
                        .weight(columnWidths.domainWeight)
                        .padding(horizontal = FloconTheme.spacing.extraSmall),
                )
                Row(
                    modifier = Modifier
                        .weight(2f)
                        .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.compact),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    state.type.image()?.let {
                        Box(
                            modifier = Modifier
                                .padding(end = FloconTheme.spacing.small)
                                .clip(RoundedCornerShape(4.dp))
                                .background(state.type.imageColor())
                                .padding(FloconTheme.spacing.extraSmall)
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
                        style = bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = errorColor ?: colors.ink,
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
                    style = floconMonoStyle(small = true),
                    color = colors.inkFaint,
                )
            }
        }
    }
}

@Composable
private fun contextualActions(
    onAction: (NetworkAction) -> Unit,
    state: NetworkItemViewState
): ImmutableList<FloconContextMenuItem> {
    val onActionCallback by rememberUpdatedState(onAction)
    // labels are resolved here: the menu is built outside of the composition
    val copyUrl = stringResource(Res.string.network_copy_url)
    val copyCurl = stringResource(Res.string.network_copy_curl)
    val createMock = stringResource(Res.string.network_create_mock)
    val replay = stringResource(Res.string.network_replay)
    val shareMarkdown = stringResource(Res.string.network_share_markdown)
    val selectItem = stringResource(Res.string.network_select_item)
    val filter = stringResource(Res.string.filter)
    val include = stringResource(Res.string.filter_include)
    val exclude = stringResource(Res.string.filter_exclude)
    val domain = stringResource(Res.string.network_header_domain)
    val query = stringResource(Res.string.network_header_query)
    val remove = stringResource(Res.string.remove)
    val removeLinesAbove = stringResource(Res.string.remove_lines_above)
    val clearOldSessions = stringResource(Res.string.clear_old_sessions)
    return remember(state, copyUrl) {
        buildMenu {
            item(label = copyUrl, onClick = { onActionCallback(NetworkAction.CopyUrl(state)) })
            if (state.type !is NetworkItemViewState.NetworkTypeUi.Grpc && state.type !is NetworkItemViewState.NetworkTypeUi.WebSocket) {
                item(
                    label = copyCurl,
                    onClick = { onActionCallback(NetworkAction.CopyCUrl(state)) }
                )
                item(
                    label = createMock,
                    onClick = { onActionCallback(NetworkAction.CreateMock(state)) }
                )
                item(
                    label = replay,
                    onClick = { onActionCallback(NetworkAction.Replay(state)) }
                )
                item(
                    label = shareMarkdown,
                    onClick = { onActionCallback(NetworkAction.ShareAsMarkdown(state)) }
                )
            }
            item(label = selectItem, onClick = { onActionCallback(NetworkAction.SelectLine(state.uuid, selected = true)) })
            separator()
            subMenu(label = filter) {
                subMenu(label = include) {
                    item(label = domain, onClick = {
                        onActionCallback(
                            NetworkAction.HeaderAction.FilterAction(
                                OnFilterAction.TextFilter(
                                    column = NetworkTextFilterColumns.Domain,
                                    action = TextFilterAction.Include(text = state.domain)
                                )
                            )
                        )
                    })
                    item(label = query, onClick = {
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
                subMenu(exclude) {
                    item(domain, onClick = {
                        onActionCallback(
                            NetworkAction.HeaderAction.FilterAction(
                                OnFilterAction.TextFilter(
                                    column = NetworkTextFilterColumns.Domain,
                                    action = TextFilterAction.Exclude(text = state.domain)
                                )
                            )
                        )
                    })
                    item(label = query, onClick = {
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
            item(label = remove, onClick = { onActionCallback(NetworkAction.Remove(state)) })
            item(
                label = removeLinesAbove,
                onClick = { onActionCallback(NetworkAction.RemoveLinesAbove(state)) }
            )
            item(
                label = clearOldSessions,
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

@Composable
fun NetworkItemViewState.NetworkTypeUi.imageColor(): Color = when (this) {
    is NetworkItemViewState.NetworkTypeUi.GraphQl,
    is NetworkItemViewState.NetworkTypeUi.Grpc,
    is NetworkItemViewState.NetworkTypeUi.Url -> null

    is NetworkItemViewState.NetworkTypeUi.WebSocket ->
        when (this.icon) {
            NetworkItemViewState.NetworkTypeUi.WebSocket.IconType.Up -> FloconTheme.colors.catNetwork
            NetworkItemViewState.NetworkTypeUi.WebSocket.IconType.Down -> FloconTheme.colors.statusSuccess
            null -> null
        }
} ?: FloconTheme.colors.statusSuccess

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
