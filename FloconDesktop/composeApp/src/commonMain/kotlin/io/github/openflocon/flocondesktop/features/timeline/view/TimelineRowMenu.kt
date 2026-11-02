package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_facet_network_path
import flocondesktop.composeapp.generated.resources.timeline_row_exclude_by
import flocondesktop.composeapp.generated.resources.timeline_row_exclude_events
import flocondesktop.composeapp.generated.resources.timeline_row_exclude_requests
import flocondesktop.composeapp.generated.resources.timeline_row_manage_exclusions
import flocondesktop.composeapp.generated.resources.timeline_row_source
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.flocondesktop.common.ui.ContextualMenu
import io.github.openflocon.flocondesktop.common.utils.primaryModifierLabel
import io.github.openflocon.flocondesktop.features.network.list.view.components.MethodTag
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOption
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOptionLabels
import io.github.openflocon.flocondesktop.features.network.options.networkItemOptions
import io.github.openflocon.flocondesktop.features.network.options.rememberNetworkItemOptionLabels
import io.github.openflocon.flocondesktop.features.timeline.mapper.toMethodUi
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import io.github.openflocon.library.designsystem.common.buildMenu
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

/**
 * The texts of the menu of a row, resolved before: the menu is built outside of the composition.
 */
internal class TimelineRowMenuLabels(
    val exclude: String, // the submenu: "Exclude requests"
    val excludeBy: String, // the title of the submenu
    val manageExclusions: String,
    val source: String, // the caption of the source line
    val sourceName: String, // ex: "Network"
    val path: String, // the caption of the path line
    val captions: Map<TimelineFacet, String>, // the caption of the line of each facet
    val copyUrlShortcut: String,
)

internal class TimelineRowMenuCallbacks(
    val onNetworkOption: (NetworkItemOption) -> Unit,
    val onExclude: (TimelineFacet, String) -> Unit,
    val onExcludePath: (String) -> Unit,
    val onExcludeSource: () -> Unit,
    val onManageExclusions: () -> Unit,
)

/**
 * The right click menu of an event. A network call has the options of the network screen first (both lines of a call,
 * request and response, have them), then every event can be excluded: what it is made of (ex: its method, its host)
 * or its whole source is hidden from the timeline.
 */
@Composable
internal fun TimelineRowMenu(
    row: TimelineRowUiModel,
    callbacks: TimelineRowMenuCallbacks,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val networkLabels = rememberNetworkItemOptionLabels()
    val labels = TimelineRowMenuLabels(
        exclude = stringResource(if (row.network != null) Res.string.timeline_row_exclude_requests else Res.string.timeline_row_exclude_events),
        excludeBy = stringResource(Res.string.timeline_row_exclude_by),
        manageExclusions = stringResource(Res.string.timeline_row_manage_exclusions),
        source = stringResource(Res.string.timeline_row_source),
        sourceName = stringResource(row.source.label()),
        path = stringResource(Res.string.timeline_facet_network_path),
        captions = row.excludes.associate { it.facet to stringResource(it.facet.title()) },
        copyUrlShortcut = "$primaryModifierLabel ⇧ C",
    )
    val items = remember(row.network, row.source, row.excludes, networkLabels, labels.exclude, labels.sourceName, callbacks) {
        timelineRowMenu(row = row, networkLabels = networkLabels, labels = labels, callbacks = callbacks)
    }
    ContextualMenu(items = items, modifier = modifier, content = content)
}

/**
 * The items of the menu: the options of the network call if any, then the submenu to exclude by.
 */
internal fun timelineRowMenu(
    row: TimelineRowUiModel,
    networkLabels: NetworkItemOptionLabels,
    labels: TimelineRowMenuLabels,
    callbacks: TimelineRowMenuCallbacks,
): ImmutableList<FloconContextMenuItem> {
    val network = row.network

    return buildMenu {
        if (network != null) {
            networkItemOptions(network.httpActions).forEach { option ->
                item(
                    label = networkLabels.of(option),
                    icon = option.icon(),
                    shortcut = labels.copyUrlShortcut.takeIf { option == NetworkItemOption.CopyUrl },
                ) { callbacks.onNetworkOption(option) }
            }
            separator()
        }
        subMenu(label = labels.exclude, icon = Icons.Outlined.VisibilityOff) {
            header(labels.excludeBy)
            if (network != null) {
                // the lines of the network, in this order: the other values are in the filter panel
                row.excludes.firstOrNull { it.facet == TimelineFacet.NetworkMethod }?.let { method ->
                    detail(labels.captions.getValue(method.facet), { callbacks.onExclude(method.facet, method.value) }) {
                        MethodTag(method = method.value.toMethodUi())
                    }
                }
                row.excludes.firstOrNull { it.facet == TimelineFacet.NetworkHost }?.let { host ->
                    detail(labels.captions.getValue(host.facet), { callbacks.onExclude(host.facet, host.value) }) {
                        MenuMonospaceText(host.value)
                    }
                }
                network.pathWithoutQuery.takeIf { it.isNotBlank() && it != "/" }?.let { path ->
                    detail(labels.path, { callbacks.onExcludePath(path) }) { MenuMonospaceText(path) }
                }
            } else {
                row.excludes.forEach { exclude ->
                    detail(labels.captions.getValue(exclude.facet), { callbacks.onExclude(exclude.facet, exclude.value) }) {
                        MenuMonospaceText(exclude.value)
                    }
                }
            }
            detail(labels.source, callbacks.onExcludeSource) { MenuText(labels.sourceName) }
            separator()
            item(label = labels.manageExclusions, icon = Icons.AutoMirrored.Outlined.Notes, onClick = callbacks.onManageExclusions)
        }
    }
}

private fun NetworkItemOption.icon(): ImageVector = when (this) {
    NetworkItemOption.CopyUrl -> Icons.Outlined.ContentCopy
    NetworkItemOption.CopyCurl -> Icons.Outlined.Terminal
    NetworkItemOption.CreateMock -> Icons.Outlined.Edit
    NetworkItemOption.Replay -> Icons.Outlined.Replay
    NetworkItemOption.ShareAsMarkdown -> Icons.Outlined.Share
}

@Composable
private fun MenuMonospaceText(text: String) {
    Text(
        text = text,
        style = FloconTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
        color = FloconTheme.colors.ink,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun MenuText(text: String) {
    Text(
        text = text,
        style = FloconTheme.typography.bodyMedium,
        color = FloconTheme.colors.ink,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
