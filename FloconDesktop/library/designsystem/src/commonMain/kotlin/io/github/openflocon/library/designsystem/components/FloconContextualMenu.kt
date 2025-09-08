@file:OptIn(ExperimentalComposeUiApi::class)

package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.ContextMenuRepresentation
import androidx.compose.foundation.ContextMenuState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.rememberPopupPositionProviderAtPosition
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem

internal class FloconMenuRepresentation : ContextMenuRepresentation {

    @Composable
    override fun Representation(
        state: ContextMenuState,
        items: () -> List<ContextMenuItem>
    ) {
        fun hide() {
            state.status = ContextMenuState.Status.Closed
        }

        val status = state.status

        if (status is ContextMenuState.Status.Open) {
            Menu(
                offset = status.rect.center,
                items = items.invoke()
                    .filterIsInstance<FloconContextMenuItem>(),
                hide = ::hide
            )
        }
    }

}

@Composable
private fun Menu(
    offset: Offset,
    items: List<FloconContextMenuItem>,
    hide: () -> Unit,
    modifier: Modifier = Modifier
) {
    val position = rememberPopupPositionProviderAtPosition(offset)

    Popup(
        onDismissRequest = hide,
        popupPositionProvider = position
    ) {
        Column(
            modifier = modifier
                .menuBackground()
        ) {
            items.forEach { item ->
                when (item) {
                    is FloconContextMenuItem.Item -> Item(item = item, hide = hide)
                    is FloconContextMenuItem.SubMenu -> SubMenu(item = item, hide = hide)
                }
            }
        }
    }
}

@Composable
private fun Item(
    item: FloconContextMenuItem.Item,
    hide: () -> Unit
) {
    FloconMenuItem(
        text = item.label,
        onClick = {
            item.onClick()
            hide()
        }
    )
}

@Composable
private fun SubMenu(
    item: FloconContextMenuItem.SubMenu,
    hide: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    var rect by remember { mutableStateOf(Rect.Zero) }

    FloconMenuItem(
        text = item.label,
        onClick = {},
        modifier = Modifier
            .hoverable(interactionSource)
            .onGloballyPositioned {
                rect = it.boundsInParent()
            }
    )

    if (hovered) {
        Menu(
            offset = rect.topRight,
            items = item.items,
            hide = hide,
            modifier = Modifier
                .hoverable(interactionSource)
                .padding(start = 4.dp)
        )
    }
}
