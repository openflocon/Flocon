package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.sharp.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.auto_scroll
import flocondesktop.composeapp.generated.resources.invert_list
import flocondesktop.composeapp.generated.resources.network_pin_panel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSettingsUiModel
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconOverflow
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TimelineOverflowMenu(
    settings: TimelineSettingsUiModel,
    onAction: (TimelineAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconOverflow(modifier = modifier) {
        FloconDropdownMenuItem(
            checked = settings.autoScroll,
            text = stringResource(Res.string.auto_scroll),
            leadingIcon = Icons.Outlined.PlayCircle,
            onCheckedChange = { checked -> onAction(TimelineAction.ToggleAutoScroll(checked)) }
        )
        FloconDropdownMenuItem(
            checked = settings.invertList,
            text = stringResource(Res.string.invert_list),
            leadingIcon = Icons.AutoMirrored.Outlined.List,
            onCheckedChange = { checked -> onAction(TimelineAction.InvertList(checked)) }
        )
        FloconDropdownMenuItem(
            checked = settings.pinPanel,
            text = stringResource(Res.string.network_pin_panel),
            leadingIcon = Icons.Sharp.PushPin,
            onCheckedChange = { checked -> onAction(TimelineAction.PinPanel(checked)) }
        )
    }
}
