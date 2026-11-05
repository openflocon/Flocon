package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.RemoveCircle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter_exclude
import flocondesktop.composeapp.generated.resources.filter_excludes
import flocondesktop.composeapp.generated.resources.filter_include
import flocondesktop.composeapp.generated.resources.filter_includes
import flocondesktop.composeapp.generated.resources.remove
import flocondesktop.composeapp.generated.resources.timeline_filters_none
import flocondesktop.composeapp.generated.resources.timeline_log_type_lifecycle
import flocondesktop.composeapp.generated.resources.timeline_log_type_navigation
import flocondesktop.composeapp.generated.resources.timeline_log_type_user_action
import flocondesktop.composeapp.generated.resources.timeline_logs_all_types
import flocondesktop.composeapp.generated.resources.timeline_logs_level
import flocondesktop.composeapp.generated.resources.timeline_logs_tag
import flocondesktop.composeapp.generated.resources.timeline_logs_tag_placeholder
import flocondesktop.composeapp.generated.resources.timeline_logs_tagged
import flocondesktop.composeapp.generated.resources.timeline_logs_types
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.flocondesktop.features.timeline.mapper.color
import io.github.openflocon.flocondesktop.features.timeline.mapper.logGlyphOf
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFacetValueUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineLogFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineLogTypeUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRuleUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineLogFilterUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconRuleChip
import io.github.openflocon.library.designsystem.components.FloconSmallIconButton
import io.github.openflocon.library.designsystem.components.FloconStatusPill
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import org.jetbrains.compose.resources.stringResource

private const val DisabledCardAlpha = 0.5f
private const val HiddenPillAlpha = 0.3f
private const val ExpandedChevronRotation = 180f
private val ChevronSize = 16.dp
private val RemoveChipIconSize = 12.dp
private val RuleButtonIconSize = 20.dp
private val SuggestionIconSize = 18.dp

/**
 * The filters of the logs, inside the Log section of the filter panel: the types menu, the tag rules and the levels.
 * The tag and the levels only narrow the tagged logs, they are dimmed and locked while the tagged logs are hidden.
 */
@Composable
internal fun LogFilterContent(
    logs: TimelineLogFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        LogTypesSelector(logs = logs, onAction = onAction)
        Column(
            modifier = Modifier.alpha(if (logs.taggedEnabled) 1f else DisabledCardAlpha),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        ) {
            LogTagSection(logs = logs, onAction = onAction)
            LogLevelPills(logs = logs, onAction = onAction)
        }
    }
}

@Composable
private fun LogSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = FloconTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = FloconTheme.colors.inkMuted,
        modifier = modifier,
    )
}

@Composable
private fun TimelineLogTypeUiModel.label(): String = when (type?.lowercase()) {
    null -> stringResource(Res.string.timeline_logs_tagged)
    "navigation" -> stringResource(Res.string.timeline_log_type_navigation)
    "useraction" -> stringResource(Res.string.timeline_log_type_user_action)
    "lifecycle" -> stringResource(Res.string.timeline_log_type_lifecycle)
    else -> type
}

private fun TimelineLogTypeUiModel.toggleAction(): TimelineFilterAction = if (type == null) {
    TimelineFilterAction.SetLogTaggedEnabled(!selected)
} else {
    TimelineFilterAction.ToggleFacet(TimelineFacet.LogType, type)
}

@Composable
private fun LogTypesSelector(
    logs: TimelineLogFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    var open by remember { mutableStateOf(false) }
    var selectorWidth by remember { mutableIntStateOf(0) }
    val selected = logs.types.filter { it.selected }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)) {
        LogSectionTitle(text = stringResource(Res.string.timeline_logs_types))
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { selectorWidth = it.width }
                    .defaultMinSize(minHeight = FloconTheme.sizes.pageBarControlHeight)
                    .clip(FloconTheme.shapes.medium)
                    .border(
                        width = 1.dp,
                        color = if (logs.allTypesSelected) colors.line else colors.accent,
                        shape = FloconTheme.shapes.medium,
                    )
                    .clickable { open = true }
                    .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.tiny),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    when {
                        logs.allTypesSelected -> Text(
                            text = stringResource(Res.string.timeline_logs_all_types, logs.totalCount),
                            style = FloconTheme.typography.bodyMedium,
                            color = colors.ink,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        selected.isEmpty() -> Text(
                            text = stringResource(Res.string.timeline_filters_none),
                            style = FloconTheme.typography.bodyMedium,
                            color = colors.inkMuted,
                            maxLines = 1,
                        )

                        else -> FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
                        ) {
                            selected.forEach { entry ->
                                LogTypeChip(entry = entry, onRemove = { onAction(entry.toggleAction()) })
                            }
                        }
                    }
                }
                FloconIcon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    modifier = Modifier
                        .size(ChevronSize)
                        .rotate(if (open) ExpandedChevronRotation else 0f),
                )
            }
            FloconDropdownMenu(
                expanded = open,
                onDismissRequest = { open = false },
                // as wide as the selector
                modifier = Modifier.width(with(LocalDensity.current) { selectorWidth.toDp() }),
            ) {
                // the menu stays open while types are ticked: several of them can be shown
                logs.types.forEach { entry ->
                    if (entry.type == null) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(colors.line),
                        )
                    }
                    LogTypeRow(entry = entry, onToggle = { onAction(entry.toggleAction()) })
                }
            }
        }
    }
}

// a type shown, with its glyph: the x hides it
@Composable
private fun LogTypeChip(
    entry: TimelineLogTypeUiModel,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val glyph = remember(entry.type) { logGlyphOf(entry.type, null) }
    val removeDescription = stringResource(Res.string.remove)
    FloconStatusPill(
        color = glyph.color(colors),
        modifier = modifier,
        leading = { LogGlyphIcon(glyph = glyph) },
    ) {
        Text(
            text = entry.label(),
            style = FloconTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = colors.ink,
            maxLines = 1,
        )
        FloconIcon(
            imageVector = Icons.Outlined.Close,
            modifier = Modifier
                .size(RemoveChipIconSize)
                .semantics { contentDescription = removeDescription }
                .clickable(onClick = onRemove),
        )
    }
}

@Composable
private fun LogTypeRow(
    entry: TimelineLogTypeUiModel,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val glyph = remember(entry.type) { logGlyphOf(entry.type, null) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(FloconTheme.sizes.menuItemHeight)
            .clickable(onClick = onToggle)
            .padding(horizontal = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        FloconCheckbox(
            checked = entry.selected,
            onCheckedChange = { onToggle() },
        )
        LogGlyphIcon(glyph = glyph)
        Text(
            text = entry.label(),
            style = FloconTheme.typography.bodyMedium,
            color = colors.ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = entry.count.toString(),
            style = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
            color = colors.inkFaint,
        )
    }
}

/**
 * The tag field with the same include / exclude concept as the network list filter: + keeps the logs with a tag
 * containing the text, − hides them, Enter includes. The known tags are suggested while typing.
 */
@Composable
private fun LogTagSection(
    logs: TimelineLogFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val enabled = logs.taggedEnabled
    val canSubmit = enabled && logs.tagQuery.isNotBlank()
    val includeDescription = stringResource(Res.string.filter_include)
    val excludeDescription = stringResource(Res.string.filter_exclude)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)) {
        LogSectionTitle(text = stringResource(Res.string.timeline_logs_tag))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
        ) {
            FloconTextFieldWithoutM3(
                value = logs.tagQuery,
                onValueChange = { onAction(TimelineFilterAction.SetLogTagQuery(it)) },
                enabled = enabled,
                keyboardActions = KeyboardActions(
                    // default action: include
                    onDone = { onAction(TimelineFilterAction.AddLogTagRule(logs.tagQuery, exclude = false)) },
                ),
                placeholder = defaultPlaceHolder(
                    stringResource(Res.string.timeline_logs_tag_placeholder),
                    color = FloconTheme.colors.inkMuted.copy(alpha = 0.5f),
                ),
                textStyle = FloconTheme.typography.bodySmall.copy(color = FloconTheme.colors.ink),
                containerColor = FloconTheme.colors.floating,
                modifier = Modifier.weight(1f),
            )
            FloconSmallIconButton(
                enabled = canSubmit,
                onClick = { onAction(TimelineFilterAction.AddLogTagRule(logs.tagQuery, exclude = false)) },
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.AddCircle,
                    modifier = Modifier
                        .size(RuleButtonIconSize)
                        .semantics { contentDescription = includeDescription },
                    tint = FloconTheme.colors.statusSuccess,
                )
            }
            FloconSmallIconButton(
                enabled = canSubmit,
                onClick = { onAction(TimelineFilterAction.AddLogTagRule(logs.tagQuery, exclude = true)) },
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.RemoveCircle,
                    modifier = Modifier
                        .size(RuleButtonIconSize)
                        .semantics { contentDescription = excludeDescription },
                    tint = FloconTheme.colors.statusError,
                )
            }
        }
        // listed right under the field instead of in a popup: a popup takes the focus away from the field being typed in
        if (enabled && logs.tagQuery.isNotBlank() && logs.tagSuggestions.isNotEmpty()) {
            TagSuggestions(suggestions = logs.tagSuggestions, onAction = onAction)
        }
        TagRules(
            title = stringResource(Res.string.filter_includes),
            rules = logs.tagIncludes,
            enabled = enabled,
            onAction = onAction,
        )
        TagRules(
            title = stringResource(Res.string.filter_excludes),
            rules = logs.tagExcludes,
            enabled = enabled,
            onAction = onAction,
        )
    }
}

@Composable
private fun TagSuggestions(
    suggestions: List<TimelineFacetValueUiModel>,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val includeDescription = stringResource(Res.string.filter_include)
    val excludeDescription = stringResource(Res.string.filter_exclude)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(FloconTheme.shapes.medium)
            .background(colors.floating)
            .border(1.dp, colors.line, FloconTheme.shapes.medium),
    ) {
        suggestions.forEach { suggestion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(TimelineFilterAction.AddLogTagRule(suggestion.value, exclude = false)) }
                    .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.extraSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            ) {
                Text(
                    text = suggestion.value,
                    style = FloconTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    color = colors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = suggestion.count.toString(),
                    style = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                    color = colors.inkFaint,
                )
                FloconSmallIconButton(
                    onClick = { onAction(TimelineFilterAction.AddLogTagRule(suggestion.value, exclude = false)) },
                ) {
                    FloconIcon(
                        imageVector = Icons.Outlined.AddCircle,
                        modifier = Modifier
                            .size(SuggestionIconSize)
                            .semantics { contentDescription = includeDescription },
                        tint = colors.statusSuccess,
                    )
                }
                FloconSmallIconButton(
                    onClick = { onAction(TimelineFilterAction.AddLogTagRule(suggestion.value, exclude = true)) },
                ) {
                    FloconIcon(
                        imageVector = Icons.Outlined.RemoveCircle,
                        modifier = Modifier
                            .size(SuggestionIconSize)
                            .semantics { contentDescription = excludeDescription },
                        tint = colors.statusError,
                    )
                }
            }
        }
    }
}

@Composable
private fun TagRules(
    title: String,
    rules: List<TimelineRuleUiModel>,
    enabled: Boolean,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (rules.isEmpty()) return
    val removeDescription = stringResource(Res.string.remove)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall)) {
        Text(
            text = title,
            style = FloconTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = FloconTheme.colors.inkMuted,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            rules.forEach { rule ->
                FloconRuleChip(
                    text = rule.value,
                    exclude = rule.exclude,
                    onRemove = { if (enabled) onAction(TimelineFilterAction.RemoveLogTagRule(rule.value)) },
                    removeContentDescription = removeDescription,
                    active = rule.active,
                    onActiveChange = { if (enabled) onAction(TimelineFilterAction.SetLogTagRuleActive(rule.value, it)) },
                )
            }
        }
    }
}

@Composable
private fun LogLevelPills(
    logs: TimelineLogFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (logs.levels.isEmpty()) return
    val colors = FloconTheme.colors
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)) {
        LogSectionTitle(text = stringResource(Res.string.timeline_logs_level))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            logs.levels.forEach { item ->
                val level = item.logLevel ?: return@forEach
                val glyph = remember(level) { logGlyphOf(null, level) }
                val color = level.color(colors)
                FloconStatusPill(
                    color = color,
                    leading = { LogGlyphIcon(glyph = glyph) },
                    modifier = Modifier
                        .alpha(if (item.selected) 1f else HiddenPillAlpha)
                        .clickable(enabled = logs.taggedEnabled) {
                            onAction(TimelineFilterAction.ToggleFacet(TimelineFacet.LogTaggedLevel, item.value))
                        },
                ) {
                    Text(
                        text = stringResource(level.label()),
                        style = FloconTheme.typography.monoSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = color,
                        maxLines = 1,
                    )
                    Text(
                        text = item.count.toString(),
                        style = FloconTheme.typography.monoSmall,
                        color = colors.inkFaint,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun LogFilterContentPreview(
    isDarkTheme: Boolean,
    allTypesSelected: Boolean = true,
    taggedEnabled: Boolean = true,
) {
    FloconTheme(isDarkTheme = isDarkTheme) {
        LogFilterContent(
            logs = remember(allTypesSelected, taggedEnabled) { previewTimelineLogFilterUiModel(allTypesSelected, taggedEnabled) },
            onAction = {},
            modifier = Modifier
                .width(PreviewContentWidth)
                .background(FloconTheme.colors.bg100)
                .padding(FloconTheme.spacing.medium),
        )
    }
}

private val PreviewContentWidth = 420.dp

@Preview
@Composable
private fun LogFilterContentAllTypesDarkPreview() = LogFilterContentPreview(isDarkTheme = true)

@Preview
@Composable
private fun LogFilterContentAllTypesLightPreview() = LogFilterContentPreview(isDarkTheme = false)

@Preview
@Composable
private fun LogFilterContentChipsDarkPreview() = LogFilterContentPreview(isDarkTheme = true, allTypesSelected = false)

@Preview
@Composable
private fun LogFilterContentChipsLightPreview() = LogFilterContentPreview(isDarkTheme = false, allTypesSelected = false)

@Preview
@Composable
private fun LogFilterContentTaggedDisabledDarkPreview() = LogFilterContentPreview(isDarkTheme = true, allTypesSelected = false, taggedEnabled = false)

@Preview
@Composable
private fun LogFilterContentTaggedDisabledLightPreview() = LogFilterContentPreview(isDarkTheme = false, allTypesSelected = false, taggedEnabled = false)
