package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_facet_accessibility_action
import flocondesktop.composeapp.generated.resources.timeline_facet_analytics_table
import flocondesktop.composeapp.generated.resources.timeline_facet_crash_exception
import flocondesktop.composeapp.generated.resources.timeline_facet_database_name
import flocondesktop.composeapp.generated.resources.timeline_facet_database_statement
import flocondesktop.composeapp.generated.resources.timeline_facet_network_content
import flocondesktop.composeapp.generated.resources.timeline_facet_network_host
import flocondesktop.composeapp.generated.resources.timeline_facet_network_method
import flocondesktop.composeapp.generated.resources.timeline_facet_network_speed
import flocondesktop.composeapp.generated.resources.timeline_facet_network_state
import flocondesktop.composeapp.generated.resources.timeline_facet_network_status
import flocondesktop.composeapp.generated.resources.timeline_filters_any
import flocondesktop.composeapp.generated.resources.timeline_filters_clear_all
import flocondesktop.composeapp.generated.resources.timeline_filters_current_session
import flocondesktop.composeapp.generated.resources.timeline_filters_no_result
import flocondesktop.composeapp.generated.resources.timeline_filters_search
import flocondesktop.composeapp.generated.resources.timeline_filters_title
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.flocondesktop.features.network.list.view.components.MethodTag
import io.github.openflocon.flocondesktop.features.timeline.mapper.toFilterPanel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFacetGroupUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFacetValueUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterPanelUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterSectionUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineSummaryUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconStatusPill
import io.github.openflocon.library.designsystem.components.FloconSwitch
import io.github.openflocon.library.designsystem.components.FloconVerticalDivider
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val PreviewPanelWidth = 500.dp
private val PreviewPanelHeight = 720.dp
private val RailButtonSize = 32.dp
private val CheckboxRowHeight = 28.dp
private const val GroupsPerRow = 2
private const val HiddenTagAlpha = 0.3f
private const val DisabledGroupsAlpha = 0.5f
private const val ExpandedChevronRotation = 180f
private const val SelectorBackgroundAlpha = 0.55f
private val SelectorChevronSize = 16.dp

/**
 * The filters of the timeline: a section by source, to show it or not and to narrow it down by facet,
 * and a rail of the sources to jump from one to the other.
 * The search of the panel looks for a filter, it does not search the events.
 */
@Composable
internal fun TimelineFilterPanel(
    panel: TimelineFilterPanelUiModel,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val query = remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val sourceLabels = TimelineSource.entries.associateWith { stringResource(it.label()) }
    val sections = remember(panel.sections, query.value, sourceLabels) {
        panel.sections.filter { it.matches(query.value, sourceLabels.getValue(it.source)) }
    }

    Row(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            PanelHeader(
                panel = panel,
                query = query,
                onQueryChange = { query.value = it },
                onAction = onAction,
                modifier = Modifier.padding(start = FloconTheme.spacing.medium, top = FloconTheme.spacing.medium, end = FloconTheme.spacing.medium),
            )
            if (sections.isEmpty()) {
                Text(
                    text = stringResource(Res.string.timeline_filters_no_result),
                    style = FloconTheme.typography.bodyMedium,
                    color = colors.inkMuted,
                    modifier = Modifier.padding(FloconTheme.spacing.medium),
                )
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
            ) {
                items(items = sections, key = { it.source }) { section ->
                    FilterSection(
                        section = section,
                        label = sourceLabels.getValue(section.source),
                        onAction = onAction,
                        // the sections slide when the order changes: a source gets its first event
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }
        FloconVerticalDivider(modifier = Modifier.fillMaxHeight())
        SourceRail(
            sections = sections,
            sourceLabels = sourceLabels,
            listState = listState,
        )
    }
}

@Composable
private fun PanelHeader(
    panel: TimelineFilterPanelUiModel,
    query: State<String>,
    onQueryChange: (String) -> Unit,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.timeline_filters_title),
                style = FloconTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.ink,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(Res.string.timeline_filters_clear_all),
                style = FloconTheme.typography.labelMedium,
                color = colors.inkMuted,
                modifier = Modifier
                    .clip(FloconTheme.shapes.small)
                    .clickable {
                        onQueryChange("")
                        onAction(TimelineFilterAction.ClearAll)
                    }
                    .padding(horizontal = FloconTheme.spacing.extraSmall, vertical = FloconTheme.spacing.tiny),
            )
        }
        FilterBar(
            filterText = query,
            placeholderText = stringResource(Res.string.timeline_filters_search),
            onTextChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.timeline_filters_current_session),
                style = FloconTheme.typography.bodyMedium,
                color = colors.ink,
                modifier = Modifier.weight(1f),
            )
            FloconSwitch(
                checked = panel.currentSessionOnly,
                onCheckedChange = { onAction(TimelineFilterAction.SetCurrentSessionOnly(it)) },
            )
        }
    }
}

@Composable
private fun FilterSection(
    section: TimelineFilterSectionUiModel,
    label: String,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val (color, _) = section.source.categoryColors()
    var expanded by remember(section.source) { mutableStateOf(section.groups.isNotEmpty()) }
    val canExpand = section.groups.isNotEmpty()
    val chevronRotation by animateFloatAsState(if (expanded) ExpandedChevronRotation else 0f)
    val labelColor by animateColorAsState(if (section.enabled) colors.ink else colors.inkMuted)
    // the filters of a source switched off are still there, to be set before switching it on
    val groupsAlpha by animateFloatAsState(if (section.enabled) 1f else DisabledGroupsAlpha)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = canExpand) { expanded = !expanded }
                .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            Box(Modifier.size(16.dp), contentAlignment = Alignment.Center) {
                if (canExpand) {
                    FloconIcon(
                        imageVector = Icons.Outlined.ExpandMore,
                        modifier = Modifier.rotate(chevronRotation),
                    )
                }
            }
            CategoryDot(color = color, filled = section.enabled)
            Text(
                text = label,
                style = FloconTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (section.activeFacetCount > 0) {
                CountBadge(count = section.activeFacetCount, color = color)
            }
            Text(
                text = section.count.toString(),
                style = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                color = colors.inkFaint,
                maxLines = 1,
            )
            FloconSwitch(
                checked = section.enabled,
                onCheckedChange = { onAction(TimelineFilterAction.ToggleSource(section.source)) },
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column(modifier = Modifier.alpha(groupsAlpha)) {
                // two groups by row: a dropdown does not need the whole width
                section.groups.chunked(GroupsPerRow).forEach { rowGroups ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = FloconTheme.spacing.large, end = FloconTheme.spacing.medium, bottom = FloconTheme.spacing.small),
                        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                    ) {
                        rowGroups.forEach { group ->
                            FacetGroup(group = group, onAction = onAction, modifier = Modifier.weight(1f))
                        }
                        // a lonely group keeps the width of the others
                        if (rowGroups.size < GroupsPerRow) {
                            Spacer(Modifier.weight((GroupsPerRow - rowGroups.size).toFloat()))
                        }
                    }
                }
            }
        }
        FloconDividerLine()
    }
}

@Composable
private fun FacetGroup(
    group: TimelineFacetGroupUiModel,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val any = stringResource(Res.string.timeline_filters_any)
    val shown = group.values.count { it.selected }

    // the menu stays open while values are unticked: a group takes several values
    Box(modifier = modifier) {
        FacetSelector(
            caption = stringResource(group.facet.title()),
            value = if (shown == group.values.size) any else "$shown/${group.values.size}",
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
        )
        FloconDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(FloconTheme.sizes.deviceDropdownWidth),
        ) {
            if (group.asTags) {
                FacetTags(
                    values = group.values,
                    onToggle = { onAction(TimelineFilterAction.ToggleFacet(group.facet, it)) },
                )
            } else {
                group.values.forEach { item ->
                    FacetValueRow(
                        item = item,
                        onToggle = { onAction(TimelineFilterAction.ToggleFacet(group.facet, item.value)) },
                    )
                }
            }
        }
    }
}

// like the page selectors, but half of the width: the caption on the left, the value and the arrow on the right
@Composable
private fun FacetSelector(
    caption: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val shape = FloconTheme.shapes.medium
    Row(
        modifier = modifier
            .height(FloconTheme.sizes.pageBarControlHeight)
            .clip(shape)
            .background(colors.bg300.copy(alpha = colors.bg300.alpha * SelectorBackgroundAlpha))
            .border(width = 1.dp, color = colors.line, shape = shape)
            .clickable(onClick = onClick)
            .padding(horizontal = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Text(
            text = caption,
            style = FloconTheme.typography.bodySmall,
            color = colors.inkFaint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = FloconTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.ink,
            maxLines = 1,
        )
        FloconIcon(
            imageVector = Icons.Outlined.KeyboardArrowDown,
            modifier = Modifier.size(SelectorChevronSize),
        )
    }
}

@Composable
private fun FacetValueRow(
    item: TimelineFacetValueUiModel,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(CheckboxRowHeight)
            .clickable(onClick = onToggle)
            .padding(horizontal = FloconTheme.spacing.small),
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
        Text(
            text = item.count.toString(),
            style = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
            color = colors.inkFaint,
        )
    }
}

/**
 * The values as the tags of the network screen, side by side: a hidden value is faded, like in the method filter of the network screen.
 */
@Composable
private fun FacetTags(
    values: List<TimelineFacetValueUiModel>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    FlowRow(
        modifier = modifier.padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.extraSmall),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        values.forEach { item ->
            val alpha by animateFloatAsState(if (item.selected) 1f else HiddenTagAlpha)
            val method = item.method
            val httpFamily = item.httpFamily
            Box(
                modifier = Modifier
                    .alpha(alpha)
                    .clip(FloconTheme.shapes.small)
                    .clickable { onToggle(item.value) },
            ) {
                when {
                    method != null -> MethodTag(method = method)
                    httpFamily != null -> FloconStatusPill(text = item.value, color = colors.httpStatus(httpFamily * 100))
                    else -> FloconStatusPill(text = item.value, color = colors.inkMuted)
                }
            }
        }
    }
}

@Composable
private fun FloconDividerLine(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(FloconTheme.colors.line)
    )
}

@Composable
private fun CountBadge(
    count: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = count.toString(),
        style = FloconTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
        fontWeight = FontWeight.SemiBold,
        color = color,
        modifier = modifier
            .background(color.copy(alpha = 0.16f), CircleShape)
            .padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny),
    )
}

/**
 * The sources, by their initials: a click goes to the section. The ones without event are set apart and faded.
 */
@Composable
private fun SourceRail(
    sections: List<TimelineFilterSectionUiModel>,
    sourceLabels: Map<TimelineSource, String>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        var separated = false
        sections.forEachIndexed { index, section ->
            if (section.count == 0 && !separated && index > 0) {
                separated = true
                Box(
                    Modifier
                        .width(RailButtonSize / 2)
                        .height(1.dp)
                        .background(FloconTheme.colors.line)
                )
            }
            RailButton(
                section = section,
                initials = sourceLabels.getValue(section.source).initials(),
                onClick = { scope.launch { listState.animateScrollToItem(index) } },
            )
        }
    }
}

@Composable
private fun RailButton(
    section: TimelineFilterSectionUiModel,
    initials: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val (color, soft) = section.source.categoryColors()
    val hasEvents = section.count > 0
    Box(
        modifier = modifier
            .size(RailButtonSize)
            .clip(FloconTheme.shapes.small)
            .then(if (section.activeFacetCount > 0) Modifier.background(soft).border(1.dp, color, FloconTheme.shapes.small) else Modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            style = FloconTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (hasEvents && section.enabled) color else colors.inkFaint,
            maxLines = 1,
        )
        if (section.activeFacetCount > 0) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(FloconTheme.spacing.tiny)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

// the section matches when the search is in the name of the source, or in one of its facet values
private fun TimelineFilterSectionUiModel.matches(query: String, label: String): Boolean {
    if (query.isBlank()) return true
    return label.contains(query, ignoreCase = true) ||
        groups.any { group -> group.values.any { it.value.contains(query, ignoreCase = true) } }
}

// "Réseau" -> "RÉ", "Base de données" -> "BD"
private fun String.initials(): String {
    val words = trim().split(' ').filter { it.isNotEmpty() }
    val letters = if (words.size > 1) words.take(2).joinToString("") { it.take(1) } else take(2)
    return letters.uppercase()
}

private fun TimelineFacet.title(): StringResource = when (this) {
    TimelineFacet.NetworkMethod -> Res.string.timeline_facet_network_method
    TimelineFacet.NetworkStatus -> Res.string.timeline_facet_network_status
    TimelineFacet.NetworkState -> Res.string.timeline_facet_network_state
    TimelineFacet.NetworkSpeed -> Res.string.timeline_facet_network_speed
    TimelineFacet.NetworkContent -> Res.string.timeline_facet_network_content
    TimelineFacet.NetworkHost -> Res.string.timeline_facet_network_host
    TimelineFacet.DatabaseName -> Res.string.timeline_facet_database_name
    TimelineFacet.DatabaseStatement -> Res.string.timeline_facet_database_statement
    TimelineFacet.AnalyticsTable -> Res.string.timeline_facet_analytics_table
    TimelineFacet.CrashException -> Res.string.timeline_facet_crash_exception
    TimelineFacet.AccessibilityAction -> Res.string.timeline_facet_accessibility_action
}

@Composable
private fun TimelineFilterPanelContentPreview(isDarkTheme: Boolean) {
    FloconTheme(isDarkTheme = isDarkTheme) {
        val panel = remember {
            previewTimelineSummaryUiModel().toFilterPanel(
                selectedSources = TimelineSource.entries.toSet(),
                facets = mapOf(TimelineFacet.NetworkStatus to setOf("5xx")),
            )
        }
        TimelineFilterPanel(
            panel = panel,
            onAction = {},
            modifier = Modifier
                .width(PreviewPanelWidth)
                .height(PreviewPanelHeight)
                .background(FloconTheme.colors.bg100),
        )
    }
}

@Preview
@Composable
private fun TimelineFilterPanelDarkPreview() = TimelineFilterPanelContentPreview(isDarkTheme = true)

@Preview
@Composable
private fun TimelineFilterPanelLightPreview() = TimelineFilterPanelContentPreview(isDarkTheme = false)
