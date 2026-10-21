@file:Suppress("UnusedReceiverParameter")

package io.github.openflocon.flocondesktop.app.ui.view.leftpannel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.MenuItem
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.MenuSection
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.MenuState
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.previewMenuState
import io.github.openflocon.flocondesktop.app.ui.view.displayName
import io.github.openflocon.library.designsystem.FloconTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.jetbrains.compose.resources.stringResource

val PanelMaxWidth = 275.dp
val PanelMinWidth = 64.dp
val PanelContentMinSize = 48.dp

@Composable
fun LeftPanelView(
    state: MenuState,
    current: SubScreen,
    expanded: Boolean,
    onClickItem: (MenuItem) -> Unit,
    modifier: Modifier = Modifier,
    onTogglePin: (MenuItem) -> Unit = {},
    onToggleSection: (MenuSection) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Box(
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colors.bg100)
            .padding(FloconTheme.spacing.small),
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            if (state.pinnedItems.isNotEmpty()) {
                // always visible, whatever the collapsed sections
                MenuItems(
                    current = current,
                    items = state.pinnedItems,
                    expanded = expanded,
                    onClickItem = onClickItem,
                    onTogglePin = onTogglePin,
                )
                Spacer(modifier = Modifier.height(FloconTheme.spacing.extraSmall))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(FloconTheme.spacing.extraSmall))
            }
            MenuSection(
                current = current,
                items = state.sections,
                expanded = expanded,
                onClickItem = onClickItem,
                onTogglePin = onTogglePin,
                onToggleSection = onToggleSection,
            )
            Spacer(modifier = Modifier.height(FloconTheme.spacing.medium))
            Spacer(Modifier.weight(1f))
            MenuItems(
                current = current,
                items = state.bottomItems,
                expanded = expanded,
                onClickItem = onClickItem,
            )
        }

        if(expanded) {
            Spacer(modifier = Modifier.width(FloconTheme.spacing.large))

            FloconVerticalScrollbar(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.TopEnd),
                adapter = rememberFloconScrollbarAdapter(scrollState),
            )
        }
    }
}

@Composable
private fun ColumnScope.MenuSection(
    current: SubScreen,
    items: ImmutableList<MenuSection>,
    expanded: Boolean,
    onClickItem: (MenuItem) -> Unit,
    onTogglePin: (MenuItem) -> Unit,
    onToggleSection: (MenuSection) -> Unit,
) {
    items.fastForEachIndexed { index, section ->
        // a collapsed section can only be reopened from its title: when the panel is reduced, everything is displayed
        val isCollapsed = section.isCollapsed && expanded
        PannelLabel(
            expanded = expanded,
            text = stringResource(section.title),
            isCollapsed = if (expanded) section.isCollapsed else null,
            onClick = { onToggleSection(section) },
        )
        AnimatedVisibility(visible = !isCollapsed) {
            Column {
                MenuItems(
                    current = current,
                    items = section.items,
                    expanded = expanded,
                    onClickItem = onClickItem,
                    onTogglePin = onTogglePin,
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.MenuItems(
    current: SubScreen,
    items: ImmutableList<MenuItem>,
    expanded: Boolean,
    onClickItem: (MenuItem) -> Unit,
    onTogglePin: (MenuItem) -> Unit = {},
) {
    items.fastForEachIndexed { index, item ->
        PanelView(
            modifier = Modifier
                .height(PanelContentMinSize)
                .fillMaxWidth(),
            icon = item.icon,
            text = stringResource(item.screen.displayName()),
            expanded = expanded,
            isSelected = current == item.screen,
            isEnabled = item.isEnabled,
            onClick = { onClickItem(item) },
            isPinnable = item.isPinnable,
            isPinned = item.isPinned,
            onTogglePin = { onTogglePin(item) },
        )
        if (index != items.lastIndex)
            Spacer(Modifier.height(FloconTheme.spacing.extraSmall))
    }
}

@Composable
@Preview
private fun LeftPanelViewPreview() {
    val selectedItem = remember { mutableStateOf<SubScreen>(SubScreen.Network) }

    FloconTheme {
        Box(modifier = Modifier.background(FloconTheme.colors.bg000))
        LeftPanelView(
            current = selectedItem.value,
            state = previewMenuState(),
            onClickItem = { selectedItem.value = it.screen },
            modifier = Modifier.wrapContentHeight(),
            expanded = false,
        )
    }
}
