@file:Suppress("UnusedReceiverParameter")

package io.github.openflocon.flocondesktop.app.ui.view.leftpannel

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
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
) {
    val scrollState = rememberScrollState()
    Box(
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colorPalette.primary)
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            MenuSection(
                current = current,
                items = state.sections.toImmutableList(),
                expanded = expanded,
                onClickItem = onClickItem,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Spacer(Modifier.weight(1f))
            MenuItems(
                current = current,
                items = state.bottomItems.toImmutableList(),
                expanded = expanded,
                onClickItem = onClickItem,
            )
        }

        if(expanded) {
            Spacer(modifier = Modifier.width(16.dp))

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
) {
    items.fastForEachIndexed { index, section ->
        PannelLabel(
            expanded = expanded,
            text = stringResource(section.title),
        )
        MenuItems(
            current = current,
            items = section.items.toImmutableList(),
            expanded = expanded,
            onClickItem = onClickItem,
        )
    }
}

@Composable
private fun ColumnScope.MenuItems(
    current: SubScreen,
    items: ImmutableList<MenuItem>,
    expanded: Boolean,
    onClickItem: (MenuItem) -> Unit,
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
        )
        if (index != items.lastIndex)
            Spacer(Modifier.height(4.dp))
    }
}

@Composable
@Preview
private fun LeftPanelViewPreview() {
    val selectedItem = remember { mutableStateOf<SubScreen>(SubScreen.Network) }

    FloconTheme {
        Box(modifier = Modifier.background(FloconTheme.colorPalette.surface))
        LeftPanelView(
            current = selectedItem.value,
            state = previewMenuState(),
            onClickItem = { selectedItem.value = it.screen },
            modifier = Modifier.wrapContentHeight(),
            expanded = false,
        )
    }
}
