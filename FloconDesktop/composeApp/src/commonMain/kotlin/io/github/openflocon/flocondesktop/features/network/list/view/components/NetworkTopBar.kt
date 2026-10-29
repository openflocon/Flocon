package io.github.openflocon.flocondesktop.features.network.list.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material.icons.outlined.WifiTethering
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter
import flocondesktop.composeapp.generated.resources.menu_item_network
import flocondesktop.composeapp.generated.resources.network_bad_network
import flocondesktop.composeapp.generated.resources.network_counter_requests
import flocondesktop.composeapp.generated.resources.network_deep_search
import flocondesktop.composeapp.generated.resources.network_display_old_sessions
import flocondesktop.composeapp.generated.resources.network_mocks_title
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocButton
import io.github.openflocon.flocondesktop.features.network.list.model.FilterUiState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkSettingsUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconIconToggleButton
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import org.jetbrains.compose.resources.stringResource

@Composable
fun NetworkTopBar(
    filterText: State<String>,
    filterState: FilterUiState,
    settings: NetworkSettingsUiModel,
    onAction: (NetworkAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconPageTopBar(
        modifier = modifier,
        title = stringResource(Res.string.menu_item_network),
        headerActions = { FeatureDocButton(doc = FeatureDoc.Network) },
        filterBar = {
            FilterBar(
                filterText = filterText,
                placeholderText = stringResource(Res.string.filter),
                onTextChange = { onAction(NetworkAction.FilterQuery(it)) },
                modifier = Modifier.weight(1f),
            )
        },
        actions = {
            FloconIconButton(
                imageVector = Icons.AutoMirrored.Filled.ManageSearch,
                onClick = { onAction(NetworkAction.OpenDeepSearch) },
                tooltip = stringResource(Res.string.network_deep_search)
            )
            FloconIconToggleButton(
                value = filterState.hasMocks,
                tooltip = stringResource(Res.string.network_mocks_title),
                onValueChange = { onAction(NetworkAction.OpenMocks) }
            ) {
                FloconIcon(imageVector = Icons.Outlined.WifiTethering)
            }
            FloconIconToggleButton(
                value = filterState.hasBadNetwork,
                tooltip = stringResource(Res.string.network_bad_network),
                onValueChange = { onAction(NetworkAction.OpenBadNetworkQuality) }
            ) {
                FloconIcon(imageVector = Icons.Outlined.SignalWifiStatusbarConnectedNoInternet4)
            }
            FloconIconToggleButton(
                value = filterState.displayOldSessions,
                tooltip = stringResource(Res.string.network_display_old_sessions),
                onValueChange = { onAction(NetworkAction.UpdateDisplayOldSessions(it)) }
            ) {
                FloconIcon(imageVector = Icons.Outlined.History)
            }
            FloconIconButton(
                imageVector = Icons.Outlined.Delete,
                onClick = { onAction(NetworkAction.Reset) }
            )
            NetworkOverflowMenu(
                settings = settings,
                onAction = onAction,
            )
        }
    )
}
