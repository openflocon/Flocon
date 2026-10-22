@file:OptIn(ExperimentalComposeUiApi::class)

package io.github.openflocon.library.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.ContextMenuRepresentation
import androidx.compose.foundation.ContextMenuState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.rememberPopupPositionProviderAtPosition
import com.composeunstyled.currentWindowContainerSize
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.roundToInt

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
        val visibleState = remember { MutableTransitionState(false) }
        // kept so the menu can still be drawn while its exit animation plays
        var lastMenu by remember { mutableStateOf<Pair<Offset, ImmutableList<FloconContextMenuItem>>?>(null) }

        if (status is ContextMenuState.Status.Open) {
            lastMenu = status.rect.center to items.invoke()
                .filterIsInstance<FloconContextMenuItem>()
                .toImmutableList()
        }
        visibleState.targetState = status is ContextMenuState.Status.Open

        val menu = lastMenu
        if (menu != null && (visibleState.currentState || visibleState.targetState)) {
            Menu(
                offset = menu.first,
                items = menu.second,
                hide = ::hide,
                visibleState = visibleState,
            )
        }
    }
}

@Composable
private fun Menu(
    offset: Offset,
    items: ImmutableList<FloconContextMenuItem>,
    hide: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    visibleState: MutableTransitionState<Boolean> = remember {
        MutableTransitionState(false).apply { targetState = true }
    },
) {
    val position = rememberPopupPositionProviderAtPosition(offset)

    Popup(
        onDismissRequest = hide,
        popupPositionProvider = position
    ) {
        AnimatedVisibility(
            visibleState = visibleState,
            enter = DropdownEnterTransition,
            exit = DropdownExitTransition,
        ) {
            Column(
                modifier = modifier
                    .menuBackground()
            ) {
                items.forEach { item ->
                    when (item) {
                        is FloconContextMenuItem.Item -> Item(item = item, hide = hide)

                        is FloconContextMenuItem.SubMenu -> SubMenu(
                            item = item,
                            hide = hide,
                            parent = interactionSource
                        )

                        is FloconContextMenuItem.Separator -> FloconMenuSeparator()
                    }
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
    hide: () -> Unit,
    parent: MutableInteractionSource?
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val enter = remember { HoverInteraction.Enter() }
    var rect by remember { mutableStateOf(Rect.Zero) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var boundsInWindow by remember { mutableStateOf(Rect.Zero) }
    val spacing = FloconTheme.spacing
    var padding by remember { mutableStateOf(PaddingValues(start = spacing.extraSmall)) }
    val window = currentWindowContainerSize()
    val density = LocalDensity.current

    LaunchedEffect(hovered) {
        if (hovered) {
            parent?.emit(enter)
        } else {
            parent?.emit(HoverInteraction.Exit(enter))
        }
    }

    Box {
        FloconMenuItem(
            text = item.label,
            onClick = {},
            trailingIcon = Icons.Outlined.ChevronRight,
            modifier = Modifier
                .hoverable(interactionSource)
                .onGloballyPositioned {
                    rect = it.boundsInParent()
                    boundsInWindow = it.boundsInWindow()
                }
        )

        if (hovered) {
            Menu(
                offset = offset,
                items = item.items.toImmutableList(),
                hide = hide,
                interactionSource = interactionSource,
                modifier = Modifier
                    .hoverable(interactionSource)
                    .padding(paddingValues = padding)
                    .onGloballyPositioned {
                        density.run {
                            offset = if ((boundsInWindow.topRight.x.roundToInt() + it.size.width) > window.width.roundToPx()) {
                                padding = PaddingValues(end = spacing.extraSmall)
                                rect.topLeft - Offset(x = it.size.width.toFloat(), y = 0f)
                            } else {
                                rect.topRight
                            }
                        }
                    }
            )
        }
    }
}
