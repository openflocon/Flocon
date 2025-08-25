@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("UnusedReceiverParameter")

package io.github.openflocon.flocondesktop.main.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.smartphone
import io.github.openflocon.flocondesktop.main.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.main.ui.model.previewDeviceItemUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCircularProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.theme.FloconColorPalette
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.skia.Image
import kotlin.io.encoding.Base64

@Composable
internal fun DeviceSelectorView(
    devicesState: DevicesStateUiModel,
    appsState: AppsStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    onAppSelected: (DeviceAppUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DeviceSelector(
            state = devicesState,
            onDeviceSelected = onDeviceSelected,
        )

        AnimatedVisibility(devicesState is DevicesStateUiModel.WithDevices) {
            DeviceAppSelector(
                devicesState = devicesState,
                appsState = appsState,
                onAppSelected = onAppSelected,
            )
        }
    }
}

@Composable
private fun DeviceAppSelector(
    devicesState: DevicesStateUiModel,
    appsState: AppsStateUiModel,
    onAppSelected: (DeviceAppUiModel) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    if (devicesState is DevicesStateUiModel.WithDevices) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = false },
        ) {
            val modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)

            appsState.appSelected?.let {
                DeviceAppName(
                    deviceApp = it,
                    onClick = { expanded = true },
                    modifier = modifier,
                )
            } ?: run {
                Selector(
                    onClick = { expanded = true },
                ) {
                    Text(
                        text = "Select",
                        modifier = modifier,
                    )
                }
            }

            when (appsState) {
                AppsStateUiModel.Empty,
                AppsStateUiModel.Loading -> {
                    // no op
                }

                is AppsStateUiModel.WithApps -> {
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.exposedDropdownSize(),
                    ) {
                        appsState.apps
                            .fastForEach { app ->
                                DeviceAppName(
                                    deviceApp = app,
                                    onClick = {
                                        onAppSelected(app)
                                        expanded = false
                                    },
                                )
                            }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceSelector(
    state: DevicesStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = it
        },
        modifier = modifier,
    ) {
        when (state) {
            DevicesStateUiModel.Empty -> Empty()
            DevicesStateUiModel.Loading -> Loading()
            is DevicesStateUiModel.WithDevices -> DeviceView(
                device = state.deviceSelected,
                onClick = {},
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            )
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = FloconTheme.colorPalette.panel,
            shadowElevation = 0.dp,
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            modifier = Modifier.exposedDropdownSize(),
        ) {
            if (state is DevicesStateUiModel.WithDevices) {
                state.devices.forEach { device ->
                    DeviceView(
                        device = device,
                        selected = state.deviceSelected.id == device.id,
                        onClick = {
                            onDeviceSelected(device)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun Selector(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(12.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .then(
                Modifier
                    .clip(shape)
                    .background(FloconTheme.colorPalette.panel)
                    .clickable(enabled = enabled, onClick = onClick)
                    .padding(contentPadding),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
        Image(
            imageVector = Icons.Outlined.KeyboardArrowDown,
            contentDescription = "",
            modifier = Modifier.width(16.dp),
            colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onSurface)
        )
    }
}

@Composable
private fun Empty() {
    Selector(
        onClick = {},
    ) {
        Text(
            text = "No Devices Found",
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
            style = FloconTheme.typography.bodyMedium,
            color = FloconTheme.colorPalette.onSurface,
        )
    }
}

@Composable
private fun Loading() {
    Selector(
        onClick = {},
    ) {
        FloconCircularProgressIndicator()
    }
}

@Composable
private fun DeviceView(
    device: DeviceItemUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
) {
    Selector(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(Res.drawable.smartphone),
                contentDescription = null,
            )

            Row(
                modifier = Modifier
                    .padding(start = 4.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = if (device.isActive) 1f else 0.4f
                        },
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = device.deviceName,
                        color = FloconTheme.colorPalette.onPanel,
                        style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    )
                    if (device.isActive.not()) {
                        Text(
                            text = "Disconnected",
                            color = FloconTheme.colorPalette.onPanel,
                            style = FloconTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                            ),
                        )
                    }
                }
                if (selected)
                    FloconIcon(
                        imageVector = Icons.Outlined.Check,
                        tint = FloconTheme.colorPalette.onPanel,
                    )
            }
        }
    }
}

@Composable
private fun DeviceAppName(
    deviceApp: DeviceAppUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Selector(
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppImage(
                deviceApp = deviceApp,
                modifier = Modifier.size(24.dp),
            )
            Column {
                Text(
                    text = deviceApp.name,
                    style = FloconTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = FloconTheme.colorPalette.onPanel,
                )
                Text(
                    text = deviceApp.packageName,
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colorPalette.onPanel.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Composable
private fun AppImage(
    deviceApp: DeviceAppUiModel,
    modifier: Modifier = Modifier
) {
    val imageBitmap = remember(deviceApp.iconEncoded) {
        deviceApp.iconEncoded?.let { encoded ->
            try {
                val decodedBytes = Base64.decode(encoded) //, Base64.DEFAULT)
                Image.makeFromEncoded(decodedBytes).toComposeImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = modifier,
        )
    } else {
        // Fallback : affiche une icône par défaut si iconEncoded est null ou invalide
        Image(
            painter = painterResource(Res.drawable.smartphone),
            contentDescription = null,
            modifier = modifier,
        )
    }
}

@Preview
@Composable
private fun DeviceViewPreview() {
    FloconTheme {
        DeviceView(
            device = previewDeviceItemUiModel(),
            onClick = {},
        )
    }
}
