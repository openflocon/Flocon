@file:Suppress("UnusedReceiverParameter")

package io.github.openflocon.flocondesktop.main.ui.view.leftpannel

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPanelItem
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPanelState
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPannelSection
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.previewLeftPannelState
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

val PanelMaxWidth = 275.dp
val PanelMinWidth = 64.dp
val PanelContentMinSize = 40.dp

@Composable
fun LeftPanelView(
    state: LeftPanelState,
    expanded: Boolean,
    onClickItem: (LeftPanelItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(FloconTheme.colorPalette.surface)
            .padding(bottom = 16.dp, top = 8.dp)
            .padding(horizontal = 12.dp),
    ) {
        MenuSection(
            items = state.sections,
            expanded = expanded,
            onClickItem = onClickItem,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(Modifier.weight(1f))
        MenuItems(
            items = state.bottomItems,
            expanded = expanded,
            onClickItem = onClickItem,
        )
    }
}

@Composable
private fun ColumnScope.MenuSection(
    items: List<LeftPannelSection>,
    expanded: Boolean,
    onClickItem: (LeftPanelItem) -> Unit,
) {
    items.fastForEachIndexed { index, section ->
        PannelLabel(
            expanded = expanded,
            text = section.title,
        )
        MenuItems(
            items = section.items,
            expanded = expanded,
            onClickItem = onClickItem,
        )
    }
}

@Composable
private fun ColumnScope.MenuItems(
    items: List<LeftPanelItem>,
    expanded: Boolean,
    onClickItem: (LeftPanelItem) -> Unit,
) {
    items.fastForEach { item ->
        PanelView(
            modifier = Modifier
                .height(PanelContentMinSize)
                .fillMaxWidth(),
            icon = item.icon,
            text = item.text,
            expanded = expanded,
            isSelected = item.isSelected,
            isEnabled = item.isEnabled,
            onClick = { onClickItem(item) },
        )
    }
}

@Composable
@Preview
private fun LeftPanelViewPreview() {
    val selectedItem = remember { mutableStateOf<String?>(null) }
    FloconTheme {
        Box(modifier = Modifier.background(Color.White))
        LeftPanelView(
            state = previewLeftPannelState(
                selectedId = selectedItem.value,
            ),
            onClickItem = {
                selectedItem.value = it.id
            },
            modifier = Modifier.wrapContentHeight(),
            expanded = false,
        )
    }
}
