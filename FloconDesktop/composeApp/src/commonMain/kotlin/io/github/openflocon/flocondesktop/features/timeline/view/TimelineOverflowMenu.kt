package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.BubbleChart
import androidx.compose.material.icons.outlined.UnfoldMore
import androidx.compose.material.icons.sharp.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.invert_list
import flocondesktop.composeapp.generated.resources.network_pin_panel
import flocondesktop.composeapp.generated.resources.timeline_cluster_events
import flocondesktop.composeapp.generated.resources.timeline_transactions_expanded
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
        // the direction of the list means nothing to the graph
        if (!settings.graph) {
            FloconDropdownMenuItem(
                checked = settings.invertList,
                text = stringResource(Res.string.invert_list),
                leadingIcon = Icons.AutoMirrored.Outlined.List,
                onCheckedChange = { checked -> onAction(TimelineAction.InvertList(checked)) }
            )
        }
        FloconDropdownMenuItem(
            checked = settings.transactionsExpandedByDefault,
            text = stringResource(Res.string.timeline_transactions_expanded),
            leadingIcon = Icons.Outlined.UnfoldMore,
            onCheckedChange = { checked -> onAction(TimelineAction.TransactionsExpandedByDefault(checked)) }
        )
        FloconDropdownMenuItem(
            checked = settings.pinPanel,
            text = stringResource(Res.string.network_pin_panel),
            leadingIcon = Icons.Sharp.PushPin,
            onCheckedChange = { checked -> onAction(TimelineAction.PinPanel(checked)) }
        )
        // the grouping of close events means nothing to the list
        if (settings.graph) {
            FloconDropdownMenuItem(
                checked = settings.clusterEvents,
                text = stringResource(Res.string.timeline_cluster_events),
                leadingIcon = Icons.Outlined.BubbleChart,
                onCheckedChange = { checked -> onAction(TimelineAction.SetClusterEvents(checked)) }
            )
        }
    }
}
