@file:Suppress("UnusedReceiverParameter")

package io.github.openflocon.flocondesktop.app.ui.view.leftpannel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.MenuItem
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.MenuState
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.MenuSection
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.previewMenuState
import io.github.openflocon.library.designsystem.FloconTheme

val PanelMaxWidth = 275.dp
val PanelMinWidth = 64.dp
val PanelContentMinSize = 40.dp

@Composable
fun LeftPanelView(
    state: MenuState,
    expanded: Boolean,
    onClickItem: (MenuItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colorPalette.primary)
            .padding(8.dp)
    ) {
        MenuSection(
            current = state.current,
            items = state.sections,
            expanded = expanded,
            onClickItem = onClickItem,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(Modifier.weight(1f))
        MenuItems(
            current = state.current,
            items = state.bottomItems,
            expanded = expanded,
            onClickItem = onClickItem,
        )
    }
}

@Composable
private fun ColumnScope.MenuSection(
    current: SubScreen,
    items: List<MenuSection>,
    expanded: Boolean,
    onClickItem: (MenuItem) -> Unit,
) {
    items.fastForEachIndexed { index, section ->
        PannelLabel(
            expanded = expanded,
            text = section.title,
        )
        MenuItems(
            current = current,
            items = section.items,
            expanded = expanded,
            onClickItem = onClickItem,
        )
    }
}

@Composable
private fun ColumnScope.MenuItems(
    current: SubScreen,
    items: List<MenuItem>,
    expanded: Boolean,
    onClickItem: (MenuItem) -> Unit,
) {
    items.fastForEachIndexed { index, item ->
        PanelView(
            modifier = Modifier
                .height(PanelContentMinSize)
                .fillMaxWidth(),
            icon = item.icon,
            text = item.text,
            expanded = expanded,
            isSelected = current == item,
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
    val selectedItem = remember { mutableStateOf(SubScreen.Network) }

    FloconTheme {
        Box(modifier = Modifier.background(Color.White))
        LeftPanelView(
            state = previewMenuState(
                current = selectedItem.value,
            ),
            onClickItem = {
//                selectedItem.value = it.screen
            },
            modifier = Modifier.wrapContentHeight(),
            expanded = false,
        )
    }
}
