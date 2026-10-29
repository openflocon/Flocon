@file:OptIn(ExperimentalFoundationApi::class)

package io.github.openflocon.flocondesktop.features.accessibility.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material.icons.outlined.CenterFocusWeak
import androidx.compose.material.icons.outlined.DragIndicator
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.accessibility_focus_activate
import flocondesktop.composeapp.generated.resources.accessibility_focus_next
import flocondesktop.composeapp.generated.resources.accessibility_focus_none
import flocondesktop.composeapp.generated.resources.accessibility_focus_position
import flocondesktop.composeapp.generated.resources.accessibility_focus_previous
import flocondesktop.composeapp.generated.resources.accessibility_focus_refresh
import flocondesktop.composeapp.generated.resources.accessibility_remote_collapse
import flocondesktop.composeapp.generated.resources.accessibility_remote_drag
import flocondesktop.composeapp.generated.resources.accessibility_remote_expand
import flocondesktop.composeapp.generated.resources.accessibility_remote_ok
import flocondesktop.composeapp.generated.resources.accessibility_remote_talkback_on
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityAction
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityFocusedNodeUiModel
import io.github.openflocon.flocondesktop.features.accessibility.model.previewAccessibilityFocusedNodeUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconOutlinedButton
import io.github.openflocon.library.designsystem.components.WithTooltip
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

private val RemoteWidth = 232.dp
private val RemoteRadius = 28.dp
private val BubbleSize = 56.dp
private val OkButtonSize = 64.dp
private val ArrowButtonWidth = 60.dp
private val ArrowButtonHeight = 64.dp
private val GetFocusedHeight = 44.dp
private val ProgressHeight = 3.dp
private val StatusDotSize = 6.dp

/**
 * The TalkBack remote, floating over the screen: it can be dragged anywhere inside [containerSize]
 * and collapsed to a bubble. The position and the collapsed state are UI state only.
 * Meant to be placed in a [Box] aligned to the bottom end.
 */
@Composable
internal fun AccessibilityRemoteOverlay(
    focusedNode: AccessibilityFocusedNodeUiModel?,
    canMoveFocus: Boolean,
    talkBackOn: Boolean,
    containerSize: IntSize,
    onAction: (AccessibilityAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    // offset from the bottom end corner (x <= 0 goes left, y <= 0 goes up)
    var offsetX by rememberSaveable { mutableFloatStateOf(0f) }
    var offsetY by rememberSaveable { mutableFloatStateOf(0f) }
    var collapsed by rememberSaveable { mutableStateOf(false) }
    var remoteSize by remember { mutableStateOf(IntSize.Zero) }
    val outsidePadding = FloconTheme.spacing.large
    val outsidePaddingPx = with(LocalDensity.current) { outsidePadding.toPx() }

    fun minX() = -(containerSize.width - remoteSize.width - 2 * outsidePaddingPx).coerceAtLeast(0f)
    fun minY() = -(containerSize.height - remoteSize.height - 2 * outsidePaddingPx).coerceAtLeast(0f)

    Box(
        modifier = modifier
            .padding(outsidePadding)
            .offset {
                IntOffset(
                    x = offsetX.coerceIn(minX(), 0f).roundToInt(),
                    y = offsetY.coerceIn(minY(), 0f).roundToInt(),
                )
            }
            .onSizeChanged { remoteSize = it },
    ) {
        val onDrag: (Offset) -> Unit = { delta ->
            offsetX = (offsetX.coerceIn(minX(), 0f) + delta.x).coerceIn(minX(), 0f)
            offsetY = (offsetY.coerceIn(minY(), 0f) + delta.y).coerceIn(minY(), 0f)
        }
        if (collapsed) {
            AccessibilityRemoteBubble(
                onClick = { collapsed = false },
                onDrag = onDrag,
            )
        } else {
            AccessibilityRemote(
                focusedNode = focusedNode,
                canMoveFocus = canMoveFocus,
                talkBackOn = talkBackOn,
                onAction = onAction,
                onCollapseClicked = { collapsed = true },
                onDrag = onDrag,
            )
        }
    }
}

/**
 * The element the screen reader is on, with the buttons to move to the previous / next one and to activate it
 */
@Composable
internal fun AccessibilityRemote(
    focusedNode: AccessibilityFocusedNodeUiModel?,
    canMoveFocus: Boolean,
    talkBackOn: Boolean,
    onAction: (AccessibilityAction) -> Unit,
    onCollapseClicked: () -> Unit,
    onDrag: (Offset) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        modifier = modifier
            .width(RemoteWidth)
            .clip(RoundedCornerShape(RemoteRadius))
            .background(FloconTheme.colors.floating)
            .border(1.dp, FloconTheme.colors.lineStrong, RoundedCornerShape(RemoteRadius))
            .padding(FloconTheme.spacing.medium),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            DragHandle(onDrag = onDrag)
            Row(
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                if (talkBackOn) {
                    Box(
                        modifier = Modifier
                            .size(StatusDotSize)
                            .background(FloconTheme.colors.statusSuccess, CircleShape),
                    )
                    Text(
                        text = stringResource(Res.string.accessibility_remote_talkback_on),
                        style = FloconTheme.typography.labelSmall,
                        color = FloconTheme.colors.inkMuted,
                    )
                }
            }
            FloconIconButton(
                imageVector = Icons.Outlined.Remove,
                tooltip = stringResource(Res.string.accessibility_remote_collapse),
                onClick = onCollapseClicked,
            )
        }

        RemoteScreen(
            focusedNode = focusedNode,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(FloconTheme.colors.bg200)
                .border(1.dp, FloconTheme.colors.line, CircleShape)
                .padding(FloconTheme.spacing.compact),
        ) {
            ArrowButton(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                tooltip = stringResource(Res.string.accessibility_focus_previous),
                shape = RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp, topEnd = 14.dp, bottomEnd = 14.dp),
                enabled = canMoveFocus,
                onClick = { onAction(AccessibilityAction.OnFocusPreviousClicked) },
            )
            OkButton(
                enabled = canMoveFocus,
                onClick = { onAction(AccessibilityAction.OnActivateClicked) },
            )
            ArrowButton(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                tooltip = stringResource(Res.string.accessibility_focus_next),
                shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp, topStart = 14.dp, bottomStart = 14.dp),
                enabled = canMoveFocus,
                onClick = { onAction(AccessibilityAction.OnFocusNextClicked) },
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small, Alignment.CenterHorizontally),
            modifier = Modifier
                .fillMaxWidth()
                .height(GetFocusedHeight)
                .clip(CircleShape)
                .border(1.dp, FloconTheme.colors.lineStrong, CircleShape)
                .clickable { onAction(AccessibilityAction.OnRefreshFocusedNodeClicked) }
                .pointerHoverIcon(PointerIcon.Hand),
        ) {
            FloconIcon(
                imageVector = Icons.Outlined.CenterFocusWeak,
                tint = FloconTheme.colors.ink,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = stringResource(Res.string.accessibility_focus_refresh),
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colors.ink,
            )
        }
    }
}

@Composable
private fun ArrowButton(
    imageVector: ImageVector,
    tooltip: String,
    shape: Shape,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    WithTooltip(tooltip) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(width = ArrowButtonWidth, height = ArrowButtonHeight)
                .clip(shape)
                .background(FloconTheme.colors.catAnalyticsSoft)
                .clickable(enabled = enabled, onClick = onClick)
                .pointerHoverIcon(PointerIcon.Hand),
        ) {
            FloconIcon(
                imageVector = imageVector,
                tint = FloconTheme.colors.catAnalytics.copy(alpha = if (enabled) 1f else 0.4f),
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

/** The dark "screen" of the remote: what the screen reader is on */
@Composable
private fun RemoteScreen(
    focusedNode: AccessibilityFocusedNodeUiModel?,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny),
        modifier = modifier
            .clip(FloconTheme.shapes.large)
            .background(FloconTheme.colors.bg000)
            .border(1.dp, FloconTheme.colors.line, FloconTheme.shapes.large)
            .padding(FloconTheme.spacing.medium),
    ) {
        if (focusedNode == null) {
            Text(
                text = stringResource(Res.string.accessibility_focus_none),
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colors.inkMuted,
            )
        } else {
            focusedNode.className?.let {
                Text(
                    text = it,
                    style = FloconTheme.typography.labelSmall,
                    color = FloconTheme.colors.inkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = focusedNode.title,
                style = FloconTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = FloconTheme.colors.ink,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            focusedNode.bounds?.let {
                Text(
                    text = it,
                    style = FloconTheme.typography.monoSmall,
                    color = FloconTheme.colors.inkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            focusedNode.position?.let { position ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = FloconTheme.spacing.small),
                ) {
                    LinearProgressIndicator(
                        progress = { position.number.toFloat() / position.count.coerceAtLeast(1) },
                        color = FloconTheme.colors.accent,
                        trackColor = FloconTheme.colors.bg300,
                        modifier = Modifier
                            .weight(1f)
                            .height(ProgressHeight)
                            .clip(CircleShape),
                    )
                    Text(
                        text = stringResource(Res.string.accessibility_focus_position, position.number, position.count),
                        style = FloconTheme.typography.monoSmall,
                        color = FloconTheme.colors.inkMuted,
                    )
                }
            }
        }
    }
}

@Composable
private fun OkButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    WithTooltip(stringResource(Res.string.accessibility_focus_activate)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(OkButtonSize)
                .clip(CircleShape)
                .background(FloconTheme.colors.catAnalytics.copy(alpha = if (enabled) 1f else 0.4f))
                .clickable(enabled = enabled, onClick = onClick)
                .pointerHoverIcon(PointerIcon.Hand),
        ) {
            Text(
                text = stringResource(Res.string.accessibility_remote_ok),
                style = FloconTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = FloconTheme.colors.floating,
            )
        }
    }
}

@Composable
private fun DragHandle(
    onDrag: (Offset) -> Unit,
    modifier: Modifier = Modifier,
) {
    WithTooltip(stringResource(Res.string.accessibility_remote_drag)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(FloconTheme.sizes.controlHeight)
                .dragToMove(onDrag),
        ) {
            FloconIcon(
                imageVector = Icons.Outlined.DragIndicator,
                tint = FloconTheme.colors.inkMuted,
            )
        }
    }
}

/** The collapsed remote: a bubble that expands on click */
@Composable
private fun AccessibilityRemoteBubble(
    onClick: () -> Unit,
    onDrag: (Offset) -> Unit,
    modifier: Modifier = Modifier,
) {
    WithTooltip(stringResource(Res.string.accessibility_remote_expand)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(BubbleSize)
                .clip(CircleShape)
                .background(FloconTheme.colors.bg100)
                .border(1.dp, FloconTheme.colors.line, CircleShape)
                .dragToMove(onDrag)
                .clickable(onClick = onClick)
                .pointerHoverIcon(PointerIcon.Hand),
        ) {
            FloconIcon(
                imageVector = Icons.Outlined.Accessibility,
                tint = FloconTheme.colors.accent,
            )
        }
    }
}

@Composable
private fun Modifier.dragToMove(onDrag: (Offset) -> Unit): Modifier {
    val currentOnDrag by rememberUpdatedState(onDrag)
    return pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
            change.consume()
            currentOnDrag(dragAmount)
        }
    }
}

@Preview
@Composable
private fun AccessibilityRemotePreview() {
    FloconTheme {
        AccessibilityRemote(
            focusedNode = previewAccessibilityFocusedNodeUiModel(),
            canMoveFocus = true,
            talkBackOn = true,
            onAction = {},
            onCollapseClicked = {},
            onDrag = {},
        )
    }
}

@Preview
@Composable
private fun AccessibilityRemoteNoneFocusedPreview() {
    FloconTheme {
        AccessibilityRemote(
            focusedNode = null,
            canMoveFocus = false,
            talkBackOn = false,
            onAction = {},
            onCollapseClicked = {},
            onDrag = {},
        )
    }
}

@Preview
@Composable
private fun AccessibilityRemoteCollapsedPreview() {
    FloconTheme {
        AccessibilityRemoteBubble(
            onClick = {},
            onDrag = {},
        )
    }
}
