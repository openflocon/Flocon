package io.github.openflocon.flocondesktop.common.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconSmallIconButton
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter

/**
 * Layout shared by the detail panels: a [header], a divider, then the sections
 */
@Composable
internal fun DetailPanel(
    header: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    val scrollAdapter = rememberFloconScrollbarAdapter(scrollState)

    Box(modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(scrollState)
                .padding(all = FloconTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
        ) {
            header()
            FloconHorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = FloconTheme.colors.bg200,
            )
            content()
        }
        FloconVerticalScrollbar(
            adapter = scrollAdapter,
            modifier = Modifier.fillMaxHeight()
                .align(Alignment.TopEnd)
        )
    }
}

/**
 * A small uppercase [label] (with optional [actions] on its right), a [title] (after an optional [icon]), then a row of [chips]
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DetailHeader(
    label: String,
    title: String,
    icon: (@Composable () -> Unit)? = null,
    chips: (@Composable RowScope.() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    SelectionContainer {
        Column(verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label.uppercase(),
                    style = FloconTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.6.sp,
                    ),
                    color = FloconTheme.colors.ink.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f),
                )
                actions?.invoke(this)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                icon?.invoke()
                Text(
                    text = title,
                    style = FloconTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = FloconTheme.colors.ink,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
            if (chips != null) {
                FlowRow(
                    verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
                    horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
                    itemVerticalAlignment = Alignment.CenterVertically,
                    content = chips,
                )
            }
        }
    }
}

@Composable
internal fun DetailChip(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colors.bg200)
            .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.extraSmall),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
internal fun DetailChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    DetailChip(modifier = modifier) {
        DetailChipText(text)
    }
}

@Composable
internal fun DetailChipText(text: String) {
    Text(
        text = text,
        style = FloconTheme.typography.bodySmall,
        color = FloconTheme.colors.inkMuted,
    )
}

/**
 * A labeled button for the toolbar of a [DetailSectionCard]
 */
@Composable
internal fun DetailActionButton(
    label: String,
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = label,
) {
    val shape = FloconTheme.shapes.medium
    Row(
        modifier = modifier
            .clip(shape)
            .background(FloconTheme.colors.bg200)
            .border(width = 1.dp, color = FloconTheme.colors.line, shape = shape)
            .clickable(onClick = onClick)
            .semantics { this.contentDescription = contentDescription }
            .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.extraSmall),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FloconIcon(
            imageVector = imageVector,
            tint = FloconTheme.colors.ink,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = label,
            style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = FloconTheme.colors.ink,
        )
    }
}

/**
 * A bordered card with a header strip: [title], an optional [badge], extra [actions] and a copy button when [onCopy] is set.
 * When [collapsible], a click on the header strip folds / unfolds the content
 */
@Composable
internal fun DetailSectionCard(
    title: String,
    badge: String?,
    copyContentDescription: String,
    onCopy: (() -> Unit)?,
    modifier: Modifier = Modifier,
    collapsible: Boolean = false,
    initiallyExpanded: Boolean = true,
    actions: (@Composable RowScope.() -> Unit)? = null,
    toolbar: (@Composable FlowRowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = FloconTheme.shapes.medium
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(width = 1.dp, color = FloconTheme.colors.bg200, shape = shape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FloconTheme.colors.bg000)
                .then(if (collapsible) Modifier.clickable { expanded = !expanded } else Modifier)
                .padding(start = FloconTheme.spacing.medium, end = FloconTheme.spacing.small, top = FloconTheme.spacing.compact, bottom = FloconTheme.spacing.compact),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            if (collapsible) {
                FloconIcon(
                    imageVector = Icons.Outlined.ExpandMore,
                    tint = FloconTheme.colors.inkMuted,
                    modifier = Modifier
                        .size(16.dp)
                        .graphicsLayer { rotationZ = if (expanded) 0f else -90f },
                )
            }
            Text(
                text = title,
                style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = FloconTheme.colors.ink,
            )
            if (badge != null) {
                Text(
                    text = badge,
                    style = FloconTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.4.sp,
                    ),
                    color = FloconTheme.colors.inkMuted,
                    modifier = Modifier
                        .clip(FloconTheme.shapes.extraSmall)
                        .background(FloconTheme.colors.bg200)
                        .padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny),
                )
            }
            Spacer(Modifier.weight(1f))
            actions?.invoke(this)
            if (onCopy != null) {
                FloconSmallIconButton(
                    imageVector = Icons.Outlined.ContentCopy,
                    onClick = onCopy,
                    contentDescription = copyContentDescription,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        if (!collapsible || expanded) {
            if (toolbar != null) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FloconTheme.colors.bg000.copy(alpha = 0.5f))
                        .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.small),
                    horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                    verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                    itemVerticalAlignment = Alignment.CenterVertically,
                    content = toolbar,
                )
            }
            content()
        }
    }
}

@Composable
internal fun DetailEmptyText(text: String) {
    Text(
        text = text,
        style = FloconTheme.typography.bodySmall,
        color = FloconTheme.colors.ink.copy(alpha = 0.6f),
        modifier = Modifier.padding(FloconTheme.spacing.medium),
    )
}

/**
 * Key / value lines separated by dividers, meant to be the content of a [DetailSectionCard]
 */
@Composable
internal fun DetailKeyValueList(
    entries: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
    keyWidth: Dp = 140.dp,
) {
    SelectionContainer(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            entries.forEachIndexed { index, (key, value) ->
                if (index != 0) {
                    FloconHorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = FloconTheme.colors.bg200,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
                    horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
                ) {
                    Text(
                        text = key,
                        style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = FloconTheme.colors.ink.copy(alpha = 0.7f),
                        modifier = Modifier.width(keyWidth),
                    )
                    Text(
                        text = value,
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colors.ink,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

internal fun List<Pair<String, String>>.toCopyableText(): String = joinToString(separator = "\n") { (key, value) -> "$key: $value" }
