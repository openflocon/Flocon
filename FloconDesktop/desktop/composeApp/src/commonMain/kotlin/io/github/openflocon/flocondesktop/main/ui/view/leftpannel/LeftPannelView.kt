@file:Suppress("UnusedReceiverParameter")

package io.github.openflocon.flocondesktop.main.ui.view.leftpannel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import flocondesktop.desktop.composeapp.generated.resources.Res
import flocondesktop.desktop.composeapp.generated.resources.app_icon_small
import io.github.openflocon.flocondesktop.main.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPanelItem
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPanelState
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPannelSection
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.previewLeftPannelState
import io.github.openflocon.flocondesktop.main.ui.model.previewDevicesStateUiModel
import io.github.openflocon.flocondesktop.main.ui.view.DeviceSelectorView
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

val PanelMaxWidth = 275.dp
val PanelMinWidth = 64.dp
val PanelContentMinSize = 40.dp

@Composable
fun LeftPanelView(
    state: LeftPanelState,
    expanded: Boolean,
    onClickItem: (LeftPanelItem) -> Unit,
    devicesState: DevicesStateUiModel, // Pass the state of devices
    onDeviceSelected: (DeviceItemUiModel) -> Unit, // Callback when a device is selected
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(FloconTheme.colorPalette.surface)
            .padding(horizontal = 12.dp, vertical = 16.dp),
    ) {
        Title(expanded = expanded)
        Spacer(modifier = Modifier.height(12.dp))
        MenuSection(
            items = state.sections,
            expanded = expanded,
            onClickItem = onClickItem
        )
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(Modifier.weight(1f))
        MenuItems(
            items = state.bottomItems,
            expanded = expanded,
            onClickItem = onClickItem
        )
        LeftPannelDivider(modifier = Modifier.padding(vertical = 12.dp))
        DeviceSelectorView(
            pannelExpanded = expanded,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            devicesState = devicesState,
            onDeviceSelected = onDeviceSelected,
        )
    }
}

@Composable
fun Title(
    expanded: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(PanelContentMinSize),
    ) {
        Image(
            modifier = Modifier
                .size(PanelContentMinSize)
                .clip(RoundedCornerShape(8.dp)),
            painter = painterResource(Res.drawable.app_icon_small),
            contentDescription = "Description de mon image",
        )
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut() + slideOutHorizontally()
        ) {
            Text(
                text = "Flocon",
                fontSize = 32.sp,
                style = FloconTheme.typography.titleLarge.copy(
                    color = FloconTheme.colorPalette.onSurface,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

@Composable
private fun ColumnScope.MenuSection(
    items: List<LeftPannelSection>,
    expanded: Boolean,
    onClickItem: (LeftPanelItem) -> Unit
) {
    items.fastForEachIndexed { index, section ->
        PannelLabel(
            expanded = expanded,
            text = section.title
        )
        MenuItems(
            items = section.items,
            expanded = expanded,
            onClickItem = onClickItem
        )
    }
}

@Composable
private fun ColumnScope.MenuItems(
    items: List<LeftPanelItem>,
    expanded: Boolean,
    onClickItem: (LeftPanelItem) -> Unit
) {
    items.fastForEach { item ->
        PannelView(
            modifier = Modifier
                .height(PanelContentMinSize)
                .fillMaxWidth(),
            icon = item.icon,
            text = item.text,
            expanded = expanded,
            isSelected = item.isSelected,
            onClick = { onClickItem(item) },
        )
    }
}

@Composable
@Preview
private fun LeftPannelViewPreview() {
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
            onDeviceSelected = {},
            expanded = false,
            devicesState = previewDevicesStateUiModel()
        )
    }
}
