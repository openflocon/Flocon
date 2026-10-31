package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter_exclude
import flocondesktop.composeapp.generated.resources.filter_include
import flocondesktop.composeapp.generated.resources.remove
import flocondesktop.composeapp.generated.resources.timeline_facet_network_content
import flocondesktop.composeapp.generated.resources.timeline_facet_network_host
import flocondesktop.composeapp.generated.resources.timeline_facet_network_method
import flocondesktop.composeapp.generated.resources.timeline_facet_network_speed
import flocondesktop.composeapp.generated.resources.timeline_facet_network_status
import flocondesktop.composeapp.generated.resources.timeline_filters_any
import flocondesktop.composeapp.generated.resources.timeline_filters_host_count
import flocondesktop.composeapp.generated.resources.timeline_filters_host_placeholder
import flocondesktop.composeapp.generated.resources.timeline_filters_hosts_matching
import flocondesktop.composeapp.generated.resources.timeline_filters_none
import flocondesktop.composeapp.generated.resources.timeline_filters_only
import flocondesktop.composeapp.generated.resources.timeline_filters_pending
import flocondesktop.composeapp.generated.resources.timeline_filters_reset
import flocondesktop.composeapp.generated.resources.timeline_filters_rule_one
import flocondesktop.composeapp.generated.resources.timeline_filters_rule_other
import flocondesktop.composeapp.generated.resources.timeline_filters_status_placeholder
import io.github.openflocon.domain.timeline.models.TIMELINE_DURATION_BUCKETS_MS
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.network.list.view.components.MethodTag
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFacetValueUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineNetworkFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRuleUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineNetworkFilterUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconDot
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconFilterChip
import io.github.openflocon.library.designsystem.components.FloconHistogram
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIncludeExcludeInput
import io.github.openflocon.library.designsystem.components.FloconRangeSlider
import io.github.openflocon.library.designsystem.components.FloconRangeSliderInset
import io.github.openflocon.library.designsystem.components.FloconRuleChip
import io.github.openflocon.library.designsystem.components.FloconStatusPill
import io.github.openflocon.library.designsystem.components.FloconToggleSwitch
import org.jetbrains.compose.resources.stringResource

private const val DisabledCardAlpha = 0.5f
private const val HiddenTagAlpha = 0.3f
private const val ExpandedChevronRotation = 180f
private const val HostRowsMaxCount = 200

// the labels under the slider, where they are on its positions: [position in the buckets] to [label]
private val DurationTicks = listOf(0L to "0", 100L to "100ms", 500L to "500ms", 1_000L to "1s")

/**
 * The filters of the network calls, side by side instead of behind dropdowns: the methods, the status, the duration,
 * the content and the hosts. The master switch keeps the filters but ignores them.
 */
@Composable
internal fun NetworkFilterCard(
    network: TimelineNetworkFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val (categoryColor, _) = TimelineSource.Network.categoryColors()
    var expanded by remember { mutableStateOf(true) }
    val chevronRotation by animateFloatAsState(if (expanded) 0f else -ExpandedChevronRotation / 2)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(FloconTheme.spacing.medium)
            .clip(FloconTheme.shapes.medium)
            .background(colors.bg200)
            .border(1.dp, colors.line, FloconTheme.shapes.medium)
            .animateContentSize()
            .padding(FloconTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(FloconTheme.shapes.small)
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            ) {
                FloconIcon(imageVector = Icons.Outlined.ExpandMore, modifier = Modifier.rotate(chevronRotation))
                CategoryDot(color = categoryColor, filled = network.enabled)
                Text(
                    text = stringResource(TimelineSource.Network.label()),
                    style = FloconTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.ink,
                    maxLines = 1,
                )
            }
            if (network.isFiltered) {
                Text(
                    text = stringResource(Res.string.timeline_filters_reset),
                    style = FloconTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.accent,
                    modifier = Modifier
                        .clip(FloconTheme.shapes.small)
                        .clickable { onAction(TimelineFilterAction.ResetNetwork) }
                        .padding(horizontal = FloconTheme.spacing.extraSmall, vertical = FloconTheme.spacing.tiny),
                )
            }
            Text(
                text = "${network.matchingCount} / ${network.totalCount}",
                style = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                color = if (network.isFiltered) colors.accent else colors.inkMuted,
                maxLines = 1,
            )
            FloconToggleSwitch(
                checked = network.enabled,
                onCheckedChange = { onAction(TimelineFilterAction.SetNetworkEnabled(it)) },
            )
        }
        AnimatedVisibility(visible = expanded) {
            // the filters of a card switched off are still there, to be set before switching it on
            Column(
                modifier = Modifier.alpha(if (network.enabled) 1f else DisabledCardAlpha),
                verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
            ) {
                MethodSection(network, onAction)
                StatusSection(network, onAction)
                DurationSection(network, onAction)
                ContentSection(network, onAction)
                HostSection(network, onAction)
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    summary: String,
    modifier: Modifier = Modifier,
    summaryActive: Boolean = false,
) {
    val colors = FloconTheme.colors
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = FloconTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = colors.inkMuted,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = summary,
            style = FloconTheme.typography.labelMedium,
            color = if (summaryActive) colors.accent else colors.inkMuted,
            maxLines = 1,
        )
    }
}

@Composable
private fun ruleCountText(count: Int): String = if (count == 1) {
    stringResource(Res.string.timeline_filters_rule_one)
} else {
    stringResource(Res.string.timeline_filters_rule_other, count)
}

// "All" or "2/5", then the rules: "All · 2 rules"
@Composable
private fun summaryText(
    shown: Int,
    total: Int,
    ruleCount: Int,
): String {
    val values = if (shown == total) stringResource(Res.string.timeline_filters_any) else "$shown/$total"
    return if (ruleCount > 0) "$values · ${ruleCountText(ruleCount)}" else values
}

@Composable
private fun MethodSection(
    network: TimelineNetworkFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)) {
        SectionTitle(
            title = stringResource(Res.string.timeline_facet_network_method),
            summary = summaryText(network.methods.count { it.selected }, network.methods.size, ruleCount = 0),
            summaryActive = network.methods.any { !it.selected },
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            network.methods.forEach { item ->
                val alpha by animateFloatAsState(if (item.selected) 1f else HiddenTagAlpha)
                item.method?.let { method ->
                    Box(
                        modifier = Modifier
                            .alpha(alpha)
                            .clip(FloconTheme.shapes.small)
                            .clickable { onAction(TimelineFilterAction.ToggleFacet(TimelineFacet.NetworkMethod, item.value)) },
                    ) {
                        MethodTag(method = method)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusSection(
    network: TimelineNetworkFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
) {
    val colors = FloconTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)) {
        SectionTitle(
            title = stringResource(Res.string.timeline_facet_network_status),
            summary = summaryText(network.statusClasses.count { it.selected }, network.statusClasses.size, network.statusRules.size),
            summaryActive = network.hasHiddenStatus || network.statusRules.isNotEmpty(),
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            network.statusClasses.forEach { item ->
                FloconFilterChip(
                    text = item.value,
                    selected = item.selected,
                    onClick = { onAction(TimelineFilterAction.ToggleFacet(TimelineFacet.NetworkStatus, item.value)) },
                    monospace = true,
                    leading = { FloconDot(color = colors.httpStatus((item.httpFamily ?: 0) * 100)) },
                )
            }
        }
        FloconIncludeExcludeInput(
            includeLabel = stringResource(Res.string.filter_include),
            excludeLabel = stringResource(Res.string.filter_exclude),
            placeholder = stringResource(Res.string.timeline_filters_status_placeholder),
            onSubmit = { text, exclude -> onAction(TimelineFilterAction.AddStatusRules(text, exclude)) },
        )
        RuleChips(
            rules = network.statusRules,
            onRemove = { onAction(TimelineFilterAction.RemoveStatusRule(it)) },
        )
    }
}

@Composable
private fun RuleChips(
    rules: List<TimelineRuleUiModel>,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (rules.isEmpty()) return
    val removeDescription = stringResource(Res.string.remove)
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        rules.forEach { rule ->
            FloconRuleChip(
                text = rule.value,
                exclude = rule.exclude,
                onRemove = { onRemove(rule.value) },
                removeContentDescription = removeDescription,
            )
        }
    }
}

@Composable
private fun DurationSection(
    network: TimelineNetworkFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
) {
    val colors = FloconTheme.colors
    val steps = network.histogram.size
    if (steps == 0) return
    Column(verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)) {
        SectionTitle(
            title = stringResource(Res.string.timeline_facet_network_speed),
            summary = network.durationLabel ?: stringResource(Res.string.timeline_filters_any),
            summaryActive = network.durationLabel != null,
        )
        FloconHistogram(
            values = network.histogram,
            start = network.durationStart,
            end = network.durationEnd,
        )
        FloconRangeSlider(
            start = network.durationStart,
            end = network.durationEnd,
            max = steps,
            onRangeChange = { start, end -> onAction(TimelineFilterAction.SetDurationRange(start, end)) },
        )
        // the ticks are where their duration is on the slider; the last position is "no limit"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = FloconRangeSliderInset),
        ) {
            val ticks = DurationTicks.map { (ms, label) -> TIMELINE_DURATION_BUCKETS_MS.indexOf(ms) to label } + (steps to "∞")
            ticks.forEach { (position, label) ->
                if (position >= 0) {
                    Text(
                        text = label,
                        style = FloconTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        color = colors.inkFaint,
                        maxLines = 1,
                        modifier = Modifier.align(BiasAlignment(horizontalBias = position.toFloat() / steps * 2 - 1, verticalBias = -1f)),
                    )
                }
            }
            // keeps the height of the labels
            Text(text = " ", style = FloconTheme.typography.labelSmall)
        }
        if (network.pendingCount > 0) {
            Text(
                text = stringResource(Res.string.timeline_filters_pending, network.pendingCount),
                style = FloconTheme.typography.bodySmall,
                color = colors.inkMuted,
            )
        }
    }
}

@Composable
private fun ContentSection(
    network: TimelineNetworkFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)) {
        SectionTitle(
            title = stringResource(Res.string.timeline_facet_network_content),
            summary = summaryText(network.content.count { it.selected }, network.content.size, ruleCount = 0),
            summaryActive = network.content.any { !it.selected },
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            network.content.forEach { item ->
                FloconFilterChip(
                    text = item.value,
                    selected = item.selected,
                    count = item.count,
                    monospace = true,
                    onClick = { onAction(TimelineFilterAction.ToggleFacet(TimelineFacet.NetworkContent, item.value)) },
                )
            }
        }
    }
}

@Composable
private fun HostSection(
    network: TimelineNetworkFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
) {
    val colors = FloconTheme.colors
    var open by remember { mutableStateOf(false) }
    var selectorWidth by remember { mutableIntStateOf(0) }
    val summary = summaryText(
        shown = network.hosts.count { it.selected },
        total = network.hosts.size,
        ruleCount = network.hostRules.size,
    )
    Column(verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)) {
        SectionTitle(
            title = stringResource(Res.string.timeline_facet_network_host),
            summary = stringResource(Res.string.timeline_filters_any).takeIf { network.hiddenHostCount == 0 && network.hostRules.isEmpty() }
                ?: summary,
            summaryActive = network.hiddenHostCount > 0 || network.hostRules.isNotEmpty(),
        )
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { selectorWidth = it.width }
                    .height(FloconTheme.sizes.pageBarControlHeight)
                    .clip(FloconTheme.shapes.medium)
                    .border(
                        width = 1.dp,
                        color = if (network.hostRules.isNotEmpty() || network.hiddenHostCount > 0) colors.accent else colors.line,
                        shape = FloconTheme.shapes.medium,
                    )
                    .clickable { open = true }
                    .padding(horizontal = FloconTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = hostSelectorText(network),
                    style = FloconTheme.typography.bodyMedium,
                    color = colors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                FloconIcon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    modifier = Modifier
                        .size(HostChevronSize)
                        .rotate(if (open) ExpandedChevronRotation else 0f),
                )
            }
            FloconDropdownMenu(
                expanded = open,
                onDismissRequest = { open = false },
                // as wide as the selector
                modifier = Modifier.width(with(LocalDensity.current) { selectorWidth.toDp() }),
            ) {
                HostMenu(network = network, onAction = onAction)
            }
        }
    }
}

private val HostChevronSize = 16.dp

// "All · 2 hosts · 2 rules"
@Composable
private fun hostSelectorText(network: TimelineNetworkFilterUiModel): String {
    val all = stringResource(Res.string.timeline_filters_any)
    val selected = network.hosts.count { it.selected }
    val values = if (network.hiddenHostCount == 0) all else "$selected/${network.hosts.size}"
    val hosts = stringResource(Res.string.timeline_filters_host_count, network.hostCount)
    return buildString {
        append(values)
        append(" · ")
        append(hosts)
        if (network.hostRules.isNotEmpty()) {
            append(" · ")
            append(ruleCountText(network.hostRules.size))
        }
    }
}

@Composable
private fun HostMenu(
    network: TimelineNetworkFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
) {
    val colors = FloconTheme.colors
    val anySelected = network.hosts.any { it.selected }
    Column(
        modifier = Modifier.padding(vertical = FloconTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = FloconTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            FloconIncludeExcludeInput(
                includeLabel = stringResource(Res.string.filter_include),
                excludeLabel = stringResource(Res.string.filter_exclude),
                placeholder = stringResource(Res.string.timeline_filters_host_placeholder),
                onSubmit = { text, exclude -> onAction(TimelineFilterAction.AddHostRule(text, exclude)) },
            )
            RuleChips(
                rules = network.hostRules,
                onRemove = { onAction(TimelineFilterAction.RemoveHostRule(it)) },
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.line),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = FloconTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.timeline_filters_hosts_matching, network.hostCount).uppercase(),
                style = FloconTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.inkMuted,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(if (anySelected) Res.string.timeline_filters_none else Res.string.timeline_filters_any),
                style = FloconTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.inkMuted,
                modifier = Modifier
                    .clip(FloconTheme.shapes.small)
                    .clickable {
                        // none: every host shown here is hidden, all: nothing is hidden
                        val hidden = if (anySelected) network.hosts.map { it.value }.toSet() else emptySet()
                        onAction(TimelineFilterAction.SetHiddenValues(TimelineFacet.NetworkHost, hidden))
                    }
                    .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.tiny),
            )
        }
        network.hosts.take(HostRowsMaxCount).forEach { item ->
            HostRow(
                item = item,
                onToggle = { onAction(TimelineFilterAction.ToggleFacet(TimelineFacet.NetworkHost, item.value)) },
                // only this one: the other hosts shown are hidden
                onOnly = {
                    onAction(TimelineFilterAction.SetHiddenValues(TimelineFacet.NetworkHost, network.hosts.map { it.value }.toSet() - item.value))
                },
            )
        }
    }
}

@Composable
private fun HostRow(
    item: TimelineFacetValueUiModel,
    onToggle: () -> Unit,
    onOnly: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        FloconCheckbox(
            checked = item.selected,
            onCheckedChange = { onToggle() },
        )
        Text(
            text = item.value,
            style = FloconTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
            color = colors.ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        FloconStatusPill(
            text = stringResource(Res.string.timeline_filters_only),
            color = colors.inkMuted,
            modifier = Modifier.clickable(onClick = onOnly),
        )
        Text(
            text = item.count.toString(),
            style = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
            color = colors.inkFaint,
        )
    }
}

@Composable
private fun NetworkFilterCardPreview(isDarkTheme: Boolean) {
    FloconTheme(isDarkTheme = isDarkTheme) {
        NetworkFilterCard(
            network = remember { previewTimelineNetworkFilterUiModel() },
            onAction = {},
            modifier = Modifier
                .width(PreviewCardWidth)
                .background(FloconTheme.colors.bg100),
        )
    }
}

private val PreviewCardWidth = 420.dp

@Preview
@Composable
private fun NetworkFilterCardDarkPreview() = NetworkFilterCardPreview(isDarkTheme = true)

@Preview
@Composable
private fun NetworkFilterCardLightPreview() = NetworkFilterCardPreview(isDarkTheme = false)
