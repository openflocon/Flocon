package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

/**
 * Common page header: one card holding the filter bar.
 *
 * Layout, top to bottom:
 * 1. bar row: [leading] actions and a divider (if [leading]), [selector], [filterBar] (takes the remaining width),
 *    [actions], then a divider and [trailing] (documentation button...) as the very last items,
 * 2. [chips] row (optional),
 * 3. [content].
 */
@Composable
fun FloconPageTopBar(
    modifier: Modifier = Modifier,
    leading: (@Composable RowScope.() -> Unit)? = null,
    selector: (@Composable RowScope.() -> Unit)? = null,
    filterBar: (@Composable RowScope.() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null,
    chips: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.(contentPadding: PaddingValues) -> Unit = {},
) {
    val hasBar = leading != null || selector != null || filterBar != null || actions != null || trailing != null

    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier
            .clip(FloconTheme.shapes.large)
            .background(FloconTheme.colors.bg100)
            .border(width = 1.dp, color = FloconTheme.colors.line, shape = FloconTheme.shapes.large)
            .padding(all = FloconTheme.spacing.small)
    ) {
        if (hasBar) {
            PageBarRow(
                leading = leading,
                selector = selector,
                filterBar = filterBar,
                actions = actions,
                trailing = trailing,
            )
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
private fun PageBarRow(
    leading: (@Composable RowScope.() -> Unit)?,
    selector: (@Composable RowScope.() -> Unit)?,
    filterBar: (@Composable RowScope.() -> Unit)?,
    actions: (@Composable RowScope.() -> Unit)?,
    trailing: (@Composable RowScope.() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
        if (selector != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
            ) {
                selector()
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
        } else {
            Spacer(Modifier.weight(1f))
        }
        if (actions != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            ) {
                actions()
            }
        }
        if (trailing != null) {
            FloconVerticalDivider(modifier = Modifier.height(FloconTheme.sizes.controlHeight))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            ) {
                trailing()
            }
        }
    }
}

@Preview
@Composable
private fun FloconPageTopBarPreview() {
    FloconTheme {
        FloconPageTopBar(
            leading = {
                FloconIconButton(imageVector = Icons.AutoMirrored.Filled.ArrowBack, onClick = {})
            },
            trailing = {
                FloconIconButton(imageVector = Icons.Outlined.Info, onClick = {})
            },
            selector = {
                FloconPageSelector(
                    icon = Icons.Outlined.TableChart,
                    caption = "Table",
                    value = "sample",
                    onClick = {},
                )
            },
            filterBar = {
                FloconTextFieldWithoutM3(
                    value = "",
                    onValueChange = {},
                    placeholder = defaultPlaceHolder("Filter"),
                    containerColor = FloconTheme.colors.bg300,
                    modifier = Modifier.weight(1f),
                )
            },
            actions = {
                FloconIconToggleButton(value = true, onValueChange = {}) {
                    FloconIcon(imageVector = Icons.Outlined.History)
                }
                FloconIconButton(imageVector = Icons.Outlined.Delete, onClick = {})
                FloconIconButton(imageVector = Icons.Outlined.MoreVert, onClick = {})
            },
            chips = { Text("Chips", color = FloconTheme.colors.ink) },
        )
    }
}
