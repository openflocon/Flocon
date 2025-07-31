package com.florent37.flocondesktop.main.ui.view.leftpannel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import com.florent37.flocondesktop.common.ui.FloconColorScheme
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.main.ui.model.DeviceItemUiModel
import com.florent37.flocondesktop.main.ui.model.DevicesStateUiModel
import com.florent37.flocondesktop.main.ui.model.leftpanel.LeftPanelItem
import com.florent37.flocondesktop.main.ui.model.leftpanel.LeftPanelState
import com.florent37.flocondesktop.main.ui.model.leftpanel.previewLeftPannelState
import com.florent37.flocondesktop.main.ui.model.previewDevicesStateUiModel
import com.florent37.flocondesktop.main.ui.view.DeviceSelectorView
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.app_icon_small
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LeftPanelView(
    state: LeftPanelState,
    onClickItem: (LeftPanelItem) -> Unit,
    devicesState: DevicesStateUiModel, // Pass the state of devices
    onDeviceSelected: (DeviceItemUiModel) -> Unit, // Callback when a device is selected
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(FloconColorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp),
        ) {
            Image(
                modifier = Modifier.size(40.dp)
                    .clip(RoundedCornerShape(8.dp)),
                painter = painterResource(Res.drawable.app_icon_small),
                contentDescription = "Description de mon image",
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Flocon",
                fontSize = 32.sp,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = FloconColorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }

        Spacer(
            modifier = Modifier.fillMaxWidth()
                .height(12.dp),
        )

        Column(
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            state.sections.fastForEachIndexed { index, section ->
                PannelLabel(
                    text = section.title,
                    modifier = if (index != 0) Modifier.padding(top = 12.dp) else Modifier,
                )
                section.items.fastForEach { item ->
                    PannelView(
                        modifier = Modifier.fillMaxWidth(),
                        icon = item.icon,
                        text = item.text,
                        isSelected = item.isSelected,
                        onClick = {
                            onClickItem(item)
                        },
                    )
                }
            }
        }
        // settings
        Column(modifier = Modifier) {
            state.bottomItems.fastForEach { item ->
                PannelView(
                    modifier = Modifier.fillMaxWidth(),
                    icon = item.icon,
                    text = item.text,
                    isSelected = item.isSelected,
                    onClick = {
                        onClickItem(item)
                    },
                )
            }

            LeftPannelDivider(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 12.dp),
            )

            DeviceSelectorView(
                modifier = Modifier.fillMaxWidth(),
                devicesState = devicesState,
                onDeviceSelected = onDeviceSelected,
            )
        }
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
            devicesState = previewDevicesStateUiModel(),
        )
    }
}
