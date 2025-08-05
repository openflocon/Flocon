@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.main.ui.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhoneDisabled
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.smartphone
import io.github.openflocon.flocondesktop.main.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.main.ui.model.previewDeviceItemUiModelPreview
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCircularProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconIcon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun DeviceSelectorView(
    pannelExpanded: Boolean,
    devicesState: DevicesStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dropDownExpanded by remember { mutableStateOf(false) }
    val topRadius by animateDpAsState(
        targetValue = if (dropDownExpanded) 0.dp else 12.dp
    )

    LaunchedEffect(pannelExpanded) {
        if (!pannelExpanded)
            dropDownExpanded = false
    }

    ExposedDropdownMenuBox(
        expanded = dropDownExpanded,
        onExpandedChange = {
            if (pannelExpanded)
                dropDownExpanded = it
        }
    ) {
        AnimatedContent(
            targetState = devicesState,
            modifier = modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(
                        topStart = topRadius,
                        topEnd = topRadius,
                        bottomEnd = 12.dp,
                        bottomStart = 12.dp
                    ),
                    clip = true
                )
                .background(color = FloconColors.pannel)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) { targetState ->
            when (targetState) {
                DevicesStateUiModel.Empty -> Empty(expanded = pannelExpanded)
                DevicesStateUiModel.Loading -> Loading()
                is DevicesStateUiModel.WithDevices -> Device(
                    state = targetState,
                    pannelExpanded = pannelExpanded
                )
            }
        }
        ExposedDropdownMenu(
            expanded = dropDownExpanded,
            onDismissRequest = { dropDownExpanded = false },
            containerColor = FloconColors.pannel, // TODO Change
            shadowElevation = 0.dp,
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            modifier = Modifier
                .exposedDropdownSize()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            if (devicesState is DevicesStateUiModel.WithDevices) {
                devicesState.devices.forEach { device ->
                    DeviceView(
                        device = device,
                        pannelExpanded = pannelExpanded,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clickable(enabled = pannelExpanded) {
                                onDeviceSelected(device)
                                dropDownExpanded = false
                            },
                    )
                }
            }
            HorizontalDivider(color = Color.LightGray) // TODO Change
        }
    }
}

@Composable
private fun Empty(
    expanded: Boolean
) {
    Crossfade(expanded) {
        if (it) {
            Text(
                text = "No Devices Found",
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colorScheme.onSurface,
            )
        } else {
            FloconIcon(
                imageVector = Icons.Outlined.PhoneDisabled,
                tint = Color.White,
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight()
            )
        }
    }

}

@Composable
private fun Loading() {
    FloconCircularProgressIndicator()
}

@Composable
private fun ExposedDropdownMenuBoxScope.Device(
    pannelExpanded: Boolean,
    state: DevicesStateUiModel.WithDevices
) {
    DeviceView(
        device = state.selected,
        pannelExpanded = pannelExpanded,
        modifier = Modifier
            .fillMaxWidth()
            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
    )
}

@Composable
private fun DeviceView(
    device: DeviceItemUiModel,
    pannelExpanded: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(Res.drawable.smartphone),
            contentDescription = null,
        )
        AnimatedVisibility(
            visible = pannelExpanded,
            exit = fadeOut(tween(100))
        ) {
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(
                    text = device.deviceName, // Device Name
                    color = FloconColorScheme.onSurface,
                    style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = device.appName,
                    color = FloconColorScheme.onSurface.copy(alpha = 0.5f),
                    style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Thin),
                )
                Text(
                    text = device.appPackageName,
                    color = FloconColorScheme.onSurface.copy(alpha = 0.5f),
                    style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Thin),
                )
            }
        }
    }
}

@Preview
@Composable
private fun DeviceViewPreview() {
    FloconTheme {
        DeviceView(
            device = previewDeviceItemUiModelPreview(),
            pannelExpanded = false
        )
    }
}
