package io.github.openflocon.library.designsystem.components

import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.theme.contentColorFor

@Composable
fun FloconListItem(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = FloconListItemDefaults.colors()
) {
    ListItem(
        headlineContent = headlineContent,
        modifier = modifier,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = colors
    )
}

data object FloconListItemDefaults {

    @Composable
    fun colors(
        containerColor: Color = FloconTheme.colorPalette.primary,
        headlineColor: Color = FloconTheme.colorPalette.contentColorFor(containerColor),
        leadingIconColor: Color = headlineColor,
        overlineColor: Color = headlineColor,
        supportingColor: Color = headlineColor,
        trailingIconColor: Color = headlineColor,
        disabledHeadlineColor: Color = headlineColor.copy(alpha = 0.5f),
        disabledLeadingIconColor: Color = headlineColor.copy(alpha = 0.5f),
        disabledTrailingIconColor: Color = headlineColor.copy(alpha = 0.5f),
    ) = ListItemDefaults.colors(
        containerColor = containerColor,
        headlineColor = headlineColor,
        leadingIconColor = leadingIconColor,
        overlineColor = overlineColor,
        supportingColor = supportingColor,
        trailingIconColor = trailingIconColor,
        disabledHeadlineColor = disabledHeadlineColor,
        disabledLeadingIconColor = disabledLeadingIconColor,
        disabledTrailingIconColor = disabledTrailingIconColor,
    )

}
