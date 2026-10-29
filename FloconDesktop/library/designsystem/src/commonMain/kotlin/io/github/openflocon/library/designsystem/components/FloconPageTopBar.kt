package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import io.github.openflocon.library.designsystem.FloconTheme

/**
 * Common page header + filter bar.
 *
 * Layout, top to bottom:
 * 1. header row (only when [title] or [headerActions] is set): [title] with an optional [subtitle] under it,
 *    and [headerActions] (documentation button...) on the right,
 * 2. bar row: [leading] actions, a divider (if [leading]), [selector], [filterBar] (takes the remaining width),
 *    then [actions],
 * 3. [chips] row, then [content].
 *
 * Without a header ([title] and [headerActions] null) the legacy layout is kept: the [selector] is on its own row
 * above the bar.
 */
@Composable
fun FloconPageTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    headerActions: (@Composable RowScope.() -> Unit)? = null,
    leading: (@Composable RowScope.() -> Unit)? = null,
    selector: (@Composable RowScope.() -> Unit)? = null,
    filterBar: (@Composable RowScope.() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    chips: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.(contentPadding: PaddingValues) -> Unit = {},
) {
    val hasHeader = title != null || headerActions != null

    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colors.bg100)
            .padding(all = FloconTheme.spacing.small)
    ) {
        if (hasHeader) {
            PageHeaderRow(
                title = title,
                subtitle = subtitle,
                headerActions = headerActions,
            )
        }
        if (selector != null && !hasHeader) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
            ) {
                selector()
            }
        }
        val inlineSelector = if (hasHeader) selector else null
        if (leading != null || inlineSelector != null || filterBar != null || actions != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            ) {
                if (leading != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                    ) {
                        leading()
                        FloconVerticalDivider(modifier = Modifier.height(FloconTheme.sizes.controlHeight))
                    }
                }
                if (inlineSelector != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
                    ) {
                        inlineSelector()
                    }
                }
                if (filterBar != null) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
                    ) {
                        filterBar()
                    }
                }
                if (actions != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                    ) {
                        actions()
                    }
                }
            }
        }
        if (chips != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            ) {
                chips()
            }
        }
        content(PaddingValues())
    }
}

@Composable
private fun PageHeaderRow(
    title: String?,
    subtitle: String?,
    headerActions: (@Composable RowScope.() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = FloconTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny),
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = FloconTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = FloconTheme.colors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = FloconTheme.typography.monoMedium,
                    color = FloconTheme.colors.inkFaint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (headerActions != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            ) {
                headerActions()
            }
        }
    }
}

@Preview
@Composable
private fun FloconPageTopBarPreview() {
    FloconTheme {
        FloconPageTopBar(
            title = "Title",
            subtitle = "342 events",
            headerActions = {
                FloconIconButton(imageVector = Icons.Outlined.Info, onClick = {})
            },
            leading = {
                FloconIconButton(imageVector = Icons.AutoMirrored.Filled.ArrowBack, onClick = {})
            },
            filterBar = { Text("Filter", color = FloconTheme.colors.ink, modifier = Modifier.weight(1f)) },
            actions = {
                FloconIconButton(imageVector = Icons.Outlined.Delete, onClick = {})
            },
            chips = { Text("Chips", color = FloconTheme.colors.ink) },
        )
    }
}
