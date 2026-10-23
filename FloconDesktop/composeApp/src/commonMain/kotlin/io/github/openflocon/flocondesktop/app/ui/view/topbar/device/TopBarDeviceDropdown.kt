package io.github.openflocon.flocondesktop.app.ui.view.topbar.device

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.device_tab_adb
import flocondesktop.composeapp.generated.resources.device_tab_empty
import flocondesktop.composeapp.generated.resources.device_tab_network
import flocondesktop.composeapp.generated.resources.no_devices_found
import io.github.openflocon.flocondesktop.app.ui.delegates.inTab
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel.ConnectionType
import io.github.openflocon.flocondesktop.app.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.app.ui.view.topbar.TopBarSelector
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCircularProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconSegmentedRow
import io.github.openflocon.library.designsystem.components.FloconTab
import io.github.openflocon.library.designsystem.components.TabType
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TopBarDeviceDropdown(
    state: DevicesStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    deleteDevice: (DeviceItemUiModel) -> Unit,
    localUrls: ImmutableList<String>,
    onCopyLocalUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    // opens on the tab of the selected device; with no device yet, on the network tab: its urls are what is needed to connect
    var tab by remember(expanded) { mutableStateOf(state.deviceSelected?.connectionType ?: ConnectionType.Network) }

    when (state) {
        DevicesStateUiModel.Loading -> Loading()

        DevicesStateUiModel.Empty -> FloconDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            onExpandRequest = { expanded = true },
            modifier = modifier,
            anchorContent = {
                TopBarSelector(
                    onClick = { expanded = true },
                    modifier = Modifier.width(FloconTheme.sizes.deviceDropdownWidth),
                ) {
                    Text(
                        text = stringResource(Res.string.no_devices_found),
                        modifier = Modifier.padding(
                            vertical = FloconTheme.spacing.extraSmall,
                            horizontal = FloconTheme.spacing.medium
                        ),
                        style = FloconTheme.typography.bodyMedium,
                        color = FloconTheme.colors.ink,
                    )
                }
            },
        ) {
            DeviceMenuContent(
                devices = emptyList(),
                selectedId = null,
                tab = tab,
                onTabChange = { tab = it },
                localUrls = localUrls,
                onCopyLocalUrl = onCopyLocalUrl,
                onDeviceSelected = {},
                deleteDevice = {},
            )
        }

        is DevicesStateUiModel.WithDevices -> FloconDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            onExpandRequest = { expanded = true },
            modifier = modifier,
            anchorContent = {
                TopBarSelector(
                    onClick = { expanded = true },
                    modifier = Modifier.width(FloconTheme.sizes.deviceDropdownWidth),
                ) {
                    TopBarDevice(device = state.deviceSelected)
                }
            },
        ) {
            DeviceMenuContent(
                devices = state.devices,
                selectedId = state.deviceSelected.id,
                tab = tab,
                onTabChange = { tab = it },
                localUrls = localUrls,
                onCopyLocalUrl = onCopyLocalUrl,
                onDeviceSelected = {
                    onDeviceSelected(it)
                    expanded = false
                },
                deleteDevice = {
                    deleteDevice(it)
                    expanded = false
                },
            )
        }
    }
}

/**
 * Two tabs: the devices connected through adb, and the ones connected over the network, with the urls of this
 * machine to give to the apps.
 */
@Composable
private fun DeviceMenuContent(
    devices: List<DeviceItemUiModel>,
    selectedId: String?,
    tab: ConnectionType,
    onTabChange: (ConnectionType) -> Unit,
    localUrls: ImmutableList<String>,
    onCopyLocalUrl: (String) -> Unit,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    deleteDevice: (DeviceItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(FloconTheme.sizes.deviceDropdownWidth)
            .padding(horizontal = FloconTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        FloconSegmentedRow {
            FloconTab(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.device_tab_adb),
                isSelected = tab == ConnectionType.Adb,
                tabType = TabType.Start,
                onSelected = { onTabChange(ConnectionType.Adb) },
            )
            FloconTab(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.device_tab_network),
                isSelected = tab == ConnectionType.Network,
                tabType = TabType.End,
                onSelected = { onTabChange(ConnectionType.Network) },
            )
        }

        if (tab == ConnectionType.Network && localUrls.isNotEmpty()) {
            TopBarLocalUrls(
                urls = localUrls,
                onCopy = onCopyLocalUrl,
            )
        }

        val tabDevices = devices.inTab(tab)
        if (tabDevices.isEmpty()) {
            Text(
                text = stringResource(Res.string.device_tab_empty),
                color = FloconTheme.colors.inkFaint,
                style = FloconTheme.typography.bodySmall,
                modifier = Modifier.padding(FloconTheme.spacing.small),
            )
        }
        tabDevices.forEach { device ->
            TopBarDevice(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(FloconTheme.shapes.medium)
                    .clickable { onDeviceSelected(device) },
                device = device,
                selected = selectedId == device.id,
                deleteClick = { deleteDevice(device) },
            )
        }
    }
}

@Composable
private fun Loading() {
    TopBarSelector(
        onClick = {},
        modifier = Modifier.width(FloconTheme.sizes.deviceDropdownWidth),
    ) {
        FloconCircularProgressIndicator()
    }
}
