package io.github.openflocon.flocondesktop.app.ui.view.topbar.device

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
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
import flocondesktop.composeapp.generated.resources.no_devices_found
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.app.ui.view.topbar.TopBarSelector
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCircularProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
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

    when (state) {
        DevicesStateUiModel.Empty -> Empty(
            localUrls = localUrls,
            onCopyLocalUrl = onCopyLocalUrl,
            modifier = modifier,
        )

        DevicesStateUiModel.Loading -> Loading()

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
            state.devices.forEach { device ->
                val isSelected = state.deviceSelected.id == device.id

                TopBarDevice(
                    modifier = Modifier
                        .width(FloconTheme.sizes.deviceDropdownWidth)
                        .clip(FloconTheme.shapes.medium)
                        .clickable {
                            onDeviceSelected(device)
                            expanded = false
                        },
                    device = device,
                    selected = isSelected,
                    deleteClick = {
                        deleteDevice(device)
                        expanded = false
                    },
                )
            }
            if (localUrls.isNotEmpty()) {
                TopBarLocalUrls(
                    urls = localUrls,
                    onCopy = onCopyLocalUrl,
                    modifier = Modifier.padding(FloconTheme.spacing.small),
                )
            }
        }
    }
}

@Composable
private fun Empty(
    localUrls: ImmutableList<String>,
    onCopyLocalUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    // with no device yet, this is where the url to put in the app is needed the most
    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onExpandRequest = { if (localUrls.isNotEmpty()) expanded = true },
        modifier = modifier,
        anchorContent = {
            TopBarSelector(
                onClick = { if (localUrls.isNotEmpty()) expanded = true },
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
        TopBarLocalUrls(
            urls = localUrls,
            onCopy = onCopyLocalUrl,
            modifier = Modifier.padding(FloconTheme.spacing.small),
        )
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
