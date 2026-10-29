package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.common.ui.ContextualMenu
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOption
import io.github.openflocon.flocondesktop.features.network.options.networkItemOptions
import io.github.openflocon.flocondesktop.features.network.options.rememberNetworkItemOptionLabels
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import io.github.openflocon.library.designsystem.common.buildMenu

/**
 * The right click menu of a network call, the one of the network screen: both lines of a call (request and response)
 * have it. The other events have no menu.
 */
@Composable
internal fun TimelineRowMenu(
    row: TimelineRowUiModel,
    onNetworkOption: (NetworkItemOption) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val network = row.network
    if (network == null) {
        content()
        return
    }
    val labels = rememberNetworkItemOptionLabels()
    val items = remember(labels, network.httpActions, onNetworkOption) {
        buildMenu { networkItemOptions(labels = labels, httpActions = network.httpActions, onOption = onNetworkOption) }
    }
    ContextualMenu(items = items, modifier = modifier, content = content)
}
