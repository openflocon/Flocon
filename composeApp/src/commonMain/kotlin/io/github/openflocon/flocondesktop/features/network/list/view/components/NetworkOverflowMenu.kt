package io.github.openflocon.flocondesktop.features.network.list.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material.icons.sharp.PushPin
import androidx.compose.material.icons.sharp.SelectAll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.composeunstyled.DropdownPanelAnchor
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.auto_scroll
import flocondesktop.composeapp.generated.resources.clear_old_sessions
import flocondesktop.composeapp.generated.resources.export_csv
import flocondesktop.composeapp.generated.resources.invert_list
import flocondesktop.composeapp.generated.resources.network_import_csv
import flocondesktop.composeapp.generated.resources.network_pin_panel
import flocondesktop.composeapp.generated.resources.network_request
import flocondesktop.composeapp.generated.resources.network_response
import flocondesktop.composeapp.generated.resources.network_select_items
import io.github.openflocon.domain.models.settings.NetworkDetailTab
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkSettingsUiModel
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconDropdownSeparator
import io.github.openflocon.library.designsystem.components.FloconMenuItem
import io.github.openflocon.library.designsystem.components.FloconOverflow
import org.jetbrains.compose.resources.stringResource

@Composable
fun NetworkOverflowMenu(
    settings: NetworkSettingsUiModel,
    onAction: (NetworkAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconOverflow(modifier = modifier) {
        FloconDropdownMenuItem(
            text = stringResource(Res.string.export_csv),
            leadingIcon = Icons.Outlined.Upload,
            onClick = { onAction(NetworkAction.ExportCsv) }
        )
        FloconDropdownMenuItem(
            text = stringResource(Res.string.network_import_csv),
            leadingIcon = Icons.Outlined.Download,
            onClick = { onAction(NetworkAction.ImportFromCsv) }
        )
        FloconDropdownMenuItem(
            checked = settings.autoScroll,
            text = stringResource(Res.string.auto_scroll),
            leadingIcon = Icons.Outlined.PlayCircle,
            onCheckedChange = { checked -> onAction(NetworkAction.ToggleAutoScroll(checked)) }
        )
        FloconDropdownMenuItem(
            checked = settings.invertList,
            text = stringResource(Res.string.invert_list),
            leadingIcon = Icons.AutoMirrored.Outlined.List,
            onCheckedChange = { checked -> onAction(NetworkAction.InvertList(checked)) }
        )
        FloconDropdownMenuItem(
            checked = settings.pinPanel,
            text = stringResource(Res.string.network_pin_panel),
            leadingIcon = Icons.Sharp.PushPin,
            onCheckedChange = { checked ->
                onAction(NetworkAction.Pinned(checked))
                it()
            }
        )
        FloconDropdownSeparator()
        DefaultDetailTabMenu(
            defaultSelectedTab = settings.defaultSelectedTab,
            onAction = onAction,
        )
        FloconDropdownMenuItem(
            text = stringResource(Res.string.network_select_items),
            leadingIcon = Icons.Sharp.SelectAll,
            onClick = { onAction(NetworkAction.MultiSelect) }
        )
        FloconDropdownSeparator()
        FloconDropdownMenuItem(
            text = stringResource(Res.string.clear_old_sessions),
            leadingIcon = Icons.Outlined.CleaningServices,
            onClick = { onAction(NetworkAction.ClearOldSession) }
        )
    }
}

@Composable
private fun DefaultDetailTabMenu(
    defaultSelectedTab: NetworkDetailTab,
    onAction: (NetworkAction) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onExpandRequest = { expanded = true },
        anchor = DropdownPanelAnchor.BottomStart,
        anchorContent = {
            FloconMenuItem(
                text = "Default detail tab: ${defaultSelectedTab.name}",
                leadingIcon = Icons.Outlined.Layers,
                trailingIcon = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                onClick = { expanded = !expanded }
            )
        }
    ) {
        FloconDropdownMenuItem(
            text = stringResource(Res.string.network_request),
            leadingIcon = Icons.Outlined.Upload,
            trailingIcon = if (defaultSelectedTab == NetworkDetailTab.Request) Icons.Outlined.Check else null,
            onClick = {
                onAction(NetworkAction.SetDefaultSelectedTab(NetworkDetailTab.Request))
                expanded = false
            }
        )
        FloconDropdownMenuItem(
            text = stringResource(Res.string.network_response),
            leadingIcon = Icons.Outlined.Download,
            trailingIcon = if (defaultSelectedTab == NetworkDetailTab.Response) Icons.Outlined.Check else null,
            onClick = {
                onAction(NetworkAction.SetDefaultSelectedTab(NetworkDetailTab.Response))
                expanded = false
            }
        )
    }
}
