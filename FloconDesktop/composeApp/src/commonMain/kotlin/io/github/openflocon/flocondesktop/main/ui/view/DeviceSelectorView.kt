@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("UnusedReceiverParameter")

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.io.encoding.Base64
import org.jetbrains.skia.Image

private val CelluleHeight = 64.dp

@Composable
internal fun ColumnScope.DeviceSelectorView(
    panelExpanded: Boolean,
    devicesState: DevicesStateUiModel,
    appsState: AppsStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    onAppSelected: (DeviceAppUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        AnimatedVisibility(devicesState is DevicesStateUiModel.WithDevices) {
            DeviceAppSelector(
                devicesState = devicesState,
                appsState = appsState,
                panelExpanded = panelExpanded,
                onAppSelected = onAppSelected,
            )
        }
        AnimatedVisibility(visible = devicesState is DevicesStateUiModel.WithDevices) {
            Spacer(Modifier.height(8.dp))
        }
        DeviceSelector(
            state = devicesState,
            panelExpanded = panelExpanded,
            onDeviceSelected = onDeviceSelected,
        )
    }
}

@Composable
private fun DeviceAppSelector(
    devicesState: DevicesStateUiModel,
    appsState: AppsStateUiModel,
    panelExpanded: Boolean,
    onAppSelected: (DeviceAppUiModel) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(panelExpanded) {
        if (!panelExpanded)
            expanded = false
    }

    if (devicesState is DevicesStateUiModel.WithDevices) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = false },
            modifier = Modifier.fillMaxWidth(),
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

            when(appsState) {
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
    panelExpanded: Boolean,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val topRadius by animateDpAsState(
        targetValue = if (expanded) 0.dp else 12.dp,
    )

    LaunchedEffect(panelExpanded) {
        if (!panelExpanded)
            expanded = false
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (panelExpanded)
                expanded = it
        },
        modifier = modifier,
    ) {
        AnimatedContent(
            targetState = state,
            contentKey = { it::class.simpleName },
            modifier = Modifier
                .fillMaxWidth()
                .height(CelluleHeight)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(
                        topStart = topRadius,
                        topEnd = topRadius,
                        bottomEnd = 12.dp,
                        bottomStart = 12.dp,
                    ),
                    clip = true,
                )
                .background(color = FloconTheme.colorPalette.panel),
        ) { targetState ->
            when (targetState) {
                DevicesStateUiModel.Empty -> Empty(expanded = panelExpanded)
                DevicesStateUiModel.Loading -> Loading()
                is DevicesStateUiModel.WithDevices -> Device(
                    state = targetState,
                    panelExpanded = panelExpanded,
                    onClick = {},
                )
            }
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
                        panelExpanded = panelExpanded,
                        selected = state.deviceSelected.id == device.id,
                        onClick = {
                            onDeviceSelected(device)
                            expanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clickable(enabled = panelExpanded) {
                            },
                    )
                }
            }
            HorizontalDivider(color = Color.LightGray) // TODO Change
        }
    }
}

@Composable
private fun Selector(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.CenterStart,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(12.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .then(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .clip(shape)
                    .background(FloconTheme.colorPalette.panel)
                    .clickable(enabled = enabled, onClick = onClick),
            ),
        contentAlignment = alignment,
        content = content,
    )
}

@Composable
private fun Empty(
    expanded: Boolean,
) {
    Selector(
        onClick = {},
    ) {
        Crossfade(expanded) {
            if (it) {
                Text(
                    text = "No Devices Found",
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
                    style = FloconTheme.typography.bodyMedium,
                    color = FloconTheme.colorPalette.onSurface,
                )
            } else {
                FloconIcon(
                    imageVector = Icons.Outlined.PhoneDisabled,
                    tint = Color.White,
                )
            }
        }
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
private fun ExposedDropdownMenuBoxScope.Device(
    panelExpanded: Boolean,
    state: DevicesStateUiModel.WithDevices,
    onClick: () -> Unit,
) {
    DeviceView(
        device = state.deviceSelected,
        panelExpanded = panelExpanded,
        onClick = onClick,
        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
    )
}

@Composable
private fun DeviceView(
    device: DeviceItemUiModel,
    panelExpanded: Boolean,
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
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(Res.drawable.smartphone),
                contentDescription = null,
            )
            AnimatedVisibility(
                visible = panelExpanded,
                exit = fadeOut(tween(100)),
            ) {
                Row(
                    modifier = Modifier.padding(start = 4.dp, end = 8.dp),
                ) {
                    Text(
                        text = device.deviceName,
                        color = FloconTheme.colorPalette.onPanel,
                        style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f),
                    )
                    if (selected)
                        FloconIcon(
                            imageVector = Icons.Outlined.Check,
                            tint = FloconTheme.colorPalette.onPanel,
                        )
                }
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
        ) {
            AppImage(
                deviceApp = deviceApp,
                modifier = Modifier.size(24.dp),
            )
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
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
    modifier : Modifier = Modifier
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
            panelExpanded = false,
            onClick = {},
        )
    }
}
