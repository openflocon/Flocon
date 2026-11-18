package io.github.openflocon.flocondesktop.features.network.list.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

private val HandleWidth = 8.dp
private val LineWidth = 2.dp

/** Thin grab zone at the right edge of a header cell: drag it horizontally to resize the column. */
@Composable
fun ColumnResizeHandle(
    onResize: (delta: Dp) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val dragged by interactionSource.collectIsDraggedAsState()

    Box(
        modifier = modifier
            .width(HandleWidth)
            .fillMaxHeight()
            .hoverable(interactionSource)
            .pointerHoverIcon(PointerIcon.Hand)
            .draggable(
                orientation = Orientation.Horizontal,
                interactionSource = interactionSource,
                state = rememberDraggableState { deltaPx -> onResize(with(density) { deltaPx.toDp() }) },
            ),
    ) {
        if (hovered || dragged) {
            Box(
                Modifier
                    .width(LineWidth)
                    .fillMaxHeight()
                    .background(FloconTheme.colors.accent),
            )
        }
    }
}
