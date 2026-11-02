package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_row_exclude
import flocondesktop.composeapp.generated.resources.timeline_row_exclude_source
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.flocondesktop.common.ui.ContextualMenu
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOption
import io.github.openflocon.flocondesktop.features.network.options.networkItemOptions
import io.github.openflocon.flocondesktop.features.network.options.rememberNetworkItemOptionLabels
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import io.github.openflocon.library.designsystem.common.buildMenu
import org.jetbrains.compose.resources.stringResource

/**
 * The right click menu of an event. A network call has the one of the network screen, both lines of a call (request and response)
 * have it. Every event can be excluded: what it is made of (ex: its method, its host) or its whole source is hidden from the timeline.
 */
@Composable
internal fun TimelineRowMenu(
    row: TimelineRowUiModel,
    onNetworkOption: (NetworkItemOption) -> Unit,
    onExclude: (TimelineFacet, String) -> Unit,
    onExcludeSource: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val network = row.network
    val networkLabels = rememberNetworkItemOptionLabels()
    val excludeLabel = stringResource(Res.string.timeline_row_exclude)
    val sourceLabel = stringResource(Res.string.timeline_row_exclude_source, stringResource(row.source.label()))
    // "Method: GET"
    val valueLabels = row.excludes.map { "${stringResource(it.facet.title())}: ${it.value}" }
    val items = remember(networkLabels, network, row.excludes, valueLabels, excludeLabel, sourceLabel, onNetworkOption, onExclude, onExcludeSource) {
        buildMenu {
            if (network != null) {
                networkItemOptions(labels = networkLabels, httpActions = network.httpActions, onOption = onNetworkOption)
                separator()
            }
            subMenu(excludeLabel) {
                row.excludes.forEachIndexed { index, exclude ->
                    item(valueLabels[index]) { onExclude(exclude.facet, exclude.value) }
                }
                if (row.excludes.isNotEmpty()) separator()
                item(sourceLabel, onExcludeSource)
            }
        }
    }
    ContextualMenu(items = items, modifier = modifier, content = content)
}
