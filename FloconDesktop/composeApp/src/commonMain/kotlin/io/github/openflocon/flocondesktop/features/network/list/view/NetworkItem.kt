package io.github.openflocon.flocondesktop.features.network.list.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.clear_old_sessions
import flocondesktop.composeapp.generated.resources.filter
import flocondesktop.composeapp.generated.resources.filter_exclude
import flocondesktop.composeapp.generated.resources.filter_include
import flocondesktop.composeapp.generated.resources.network_copy_curl
import flocondesktop.composeapp.generated.resources.network_copy_url
import flocondesktop.composeapp.generated.resources.network_create_mock
import flocondesktop.composeapp.generated.resources.network_header_host
import flocondesktop.composeapp.generated.resources.network_header_request
import flocondesktop.composeapp.generated.resources.network_image_response
import flocondesktop.composeapp.generated.resources.network_mock_tag
import flocondesktop.composeapp.generated.resources.network_replay
import flocondesktop.composeapp.generated.resources.network_select_item
import flocondesktop.composeapp.generated.resources.network_share_markdown
import flocondesktop.composeapp.generated.resources.remove
import flocondesktop.composeapp.generated.resources.remove_lines_above
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.flocondesktop.common.ui.ContextualMenu
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemColumnWidths
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.header.OnFilterAction
import io.github.openflocon.flocondesktop.features.network.list.model.header.TextFilterAction
import io.github.openflocon.flocondesktop.features.network.list.model.previewExceptionItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.previewGrpcItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.previewImageItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.previewMockedItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.previewNetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.previewNetworkItemViewStateError
import io.github.openflocon.flocondesktop.features.network.list.view.components.MethodTag
import io.github.openflocon.flocondesktop.features.network.list.view.components.StatusCell
import io.github.openflocon.flocondesktop.features.network.list.view.components.isFatalError
import io.github.openflocon.flocondesktop.features.timeline.view.Rail
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import io.github.openflocon.library.designsystem.common.buildMenu
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconSurface
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NetworkItem(
    state: NetworkItemViewState,
    multiSelect: Boolean,
    multiSelected: Boolean,
    selected: Boolean,
    onAction: (NetworkAction) -> Unit,
    modifier: Modifier = Modifier,
    columnWidths: NetworkItemColumnWidths = NetworkItemColumnWidths(), // Default widths provided
) {
    val colors = FloconTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()

    ContextualMenu(
        items = contextualActions(
            onAction = onAction,
            state = state,
        ),
    ) {
        val shape = FloconTheme.shapes.medium
        val isError = state.status.isFatalError()
        val highlighted = selected || hovered
        Row(
            modifier = modifier
                .then(
                    if (state.isFromOldAppInstance) {
                        Modifier.alpha(0.4f)
                    } else {
                        Modifier
                    }
                )
                .height(IntrinsicSize.Min)
                .background(
                    when {
                        highlighted -> colors.bg300
                        isError -> colors.statusError.copy(alpha = 0.10f)
                        state.isReplayed -> colors.catNetworkSoft
                        state.isMocked -> colors.accent.copy(alpha = 0.05f)
                        else -> Color.Transparent
                    },
                    shape,
                )
                .then(if (selected) Modifier.border(1.dp, colors.lineStrong, shape) else Modifier)
                .hoverable(interactionSource)
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { onAction(NetworkAction.SelectRequest(state.uuid)) },
                    onDoubleClick = { onAction(NetworkAction.DoubleClicked(state)) }
                )
                .padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny),
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
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    state.dateFormatted,
                    style = FloconTheme.typography.monoMedium,
                    fontWeight = FontWeight.Medium,
                    color = colors.ink,
                    maxLines = 1,
                )
            }

            // Method - Fixed width for the tag from data class
            Box(
                modifier = Modifier.width(columnWidths.methodWidth),
                contentAlignment = Alignment.Center,
            ) {
                MethodTag(method = state.method)
            }

            // Domain - Width from data class
            Text(
                text = state.domain,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = FloconTheme.typography.bodySmall,
                fontSize = 12.sp,
                color = colors.inkFaint,
                modifier = Modifier
                    .width(columnWidths.domainWidth)
                    .padding(horizontal = FloconTheme.spacing.extraSmall),
            )

            RequestCell(
                state = state,
                modifier = Modifier.weight(1f),
            )

            StatusCell(
                status = state.status,
                modifier = Modifier.width(columnWidths.statusCodeWidth)
            )

            Box(
                modifier = Modifier.width(columnWidths.timeWidth),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Text(
                    state.timeFormatted ?: "",
                    style = FloconTheme.typography.monoMedium,
                    color = colors.inkFaint,
                    maxLines = 1,
                )
            }
        }
    }
}

// the request filling its column: tags (mock, image), the path, then the error of an exception
@Composable
private fun RequestCell(
    state: NetworkItemViewState,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors

    Row(
        modifier = modifier.heightIn(min = FloconTheme.sizes.rowHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        state.type.image()?.let {
            Box(
                modifier = Modifier
                    .padding(end = FloconTheme.spacing.small)
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
        if (state.isMocked) {
            Text(
                text = stringResource(Res.string.network_mock_tag),
                style = FloconTheme.typography.monoSmall,
                fontWeight = FontWeight.Bold,
                color = colors.accent,
                maxLines = 1,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(colors.accent.copy(alpha = 0.16f))
                    .padding(horizontal = 6.dp, vertical = 1.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        if (state.isImage) {
            Box(
                modifier = Modifier
                    .size(width = 20.dp, height = 18.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(colors.catNetwork.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = stringResource(Res.string.network_image_response),
                    tint = colors.catNetwork,
                    modifier = Modifier.size(12.dp),
                )
            }
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = requestText(text = state.type.query, queryColor = colors.inkFaint),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = FloconTheme.typography.bodySmall,
            fontSize = 12.sp,
            color = colors.ink,
            // the path gives way to the error message only when both do not fit
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(FloconTheme.shapes.medium)
                .background(colors.bg100)
                .padding(FloconTheme.spacing.small)
        )
        state.errorMessage?.let { message ->
            Text(
                text = message,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = FloconTheme.typography.bodySmall,
                fontSize = 12.sp,
                color = colors.statusError,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f, fill = false),
            )
        }
    }
}

// the path in the text color, the query string ('?...') in a fainter one
private fun requestText(text: String, queryColor: Color): AnnotatedString {
    val queryStart = text.indexOf('?')
    if (queryStart < 0) return AnnotatedString(text)
    return buildAnnotatedString {
        append(text.substring(0, queryStart))
        withStyle(SpanStyle(color = queryColor)) { append(text.substring(queryStart)) }
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
    val domain = stringResource(Res.string.network_header_host)
    val query = stringResource(Res.string.network_header_request)
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
private fun ItemPreview(
    state: NetworkItemViewState,
    selected: Boolean = false,
) {
    FloconTheme {
        FloconSurface {
            NetworkItem(
                modifier = Modifier.fillMaxWidth(),
                selected = selected,
                multiSelect = false,
                multiSelected = false,
                state = state,
                onAction = {},
            )
        }
    }
}

@Composable
@Preview
private fun ItemPreview() = ItemPreview(previewNetworkItemViewState())

@Composable
@Preview
private fun ItemViewSelectedPreview() = ItemPreview(previewNetworkItemViewState(), selected = true)

@Composable
@Preview
private fun ItemViewMockedPreview() = ItemPreview(previewMockedItemViewState())

@Composable
@Preview
private fun ItemViewImagePreview() = ItemPreview(previewImageItemViewState())

@Composable
@Preview
private fun ItemViewError500Preview() = ItemPreview(previewNetworkItemViewStateError())

@Composable
@Preview
private fun ItemViewExceptionPreview() = ItemPreview(previewExceptionItemViewState())

@Composable
@Preview
private fun ItemViewGrpcPreview() = ItemPreview(previewGrpcItemViewState())
