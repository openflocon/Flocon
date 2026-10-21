package io.github.openflocon.flocondesktop.common.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
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

    Box(
        modifier.background(FloconTheme.colorPalette.primary)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(scrollState)
                .padding(all = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            header()
            FloconHorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = FloconTheme.colorPalette.secondary,
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
 * A small uppercase [label], a [title], then a row of [chips]
 */
@Composable
internal fun DetailHeader(
    label: String,
    title: String,
    chips: (@Composable RowScope.() -> Unit)? = null,
) {
    SelectionContainer {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = label.uppercase(),
                style = FloconTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.6.sp,
                ),
                color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.6f),
            )
            Text(
                text = title,
                style = FloconTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = FloconTheme.colorPalette.onPrimary,
            )
            if (chips != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
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
            .background(FloconTheme.colorPalette.secondary)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
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
        color = FloconTheme.colorPalette.onSecondary,
    )
}

/**
 * A bordered card with a header strip: [title], an optional [badge] and a copy button when [onCopy] is set
 */
@Composable
internal fun DetailSectionCard(
    title: String,
    badge: String?,
    copyContentDescription: String,
    onCopy: (() -> Unit)?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = FloconTheme.shapes.medium
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(width = 1.dp, color = FloconTheme.colorPalette.secondary, shape = shape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FloconTheme.colorPalette.surface)
                .padding(start = 12.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = FloconTheme.colorPalette.onSurface,
            )
            if (badge != null) {
                Text(
                    text = badge,
                    style = FloconTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.4.sp,
                    ),
                    color = FloconTheme.colorPalette.onSecondary,
                    modifier = Modifier
                        .clip(FloconTheme.shapes.extraSmall)
                        .background(FloconTheme.colorPalette.secondary)
                        .padding(horizontal = 6.dp, vertical = 1.dp),
                )
            }
            Spacer(Modifier.weight(1f))
            if (onCopy != null) {
                FloconSmallIconButton(
                    imageVector = Icons.Outlined.ContentCopy,
                    onClick = onCopy,
                    contentDescription = copyContentDescription,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        content()
    }
}

@Composable
internal fun DetailEmptyText(text: String) {
    Text(
        text = text,
        style = FloconTheme.typography.bodySmall,
        color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.6f),
        modifier = Modifier.padding(12.dp),
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
                        color = FloconTheme.colorPalette.secondary,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = key,
                        style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.7f),
                        modifier = Modifier.width(keyWidth),
                    )
                    Text(
                        text = value,
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colorPalette.onPrimary,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

internal fun List<Pair<String, String>>.toCopyableText(): String = joinToString(separator = "\n") { (key, value) -> "$key: $value" }
