package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

private val SelectorIconSize = 16.dp

/**
 * Selector button of a page ([FloconPageTopBar] `selector` slot): same height and shape as the filter field,
 * a 1dp border over a slightly lighter background, then a leading [icon], a faint [caption], the [value] in
 * semibold and a chevron. [onClick] usually opens a dropdown menu anchored on it.
 */
@Composable
fun FloconPageSelector(
    icon: ImageVector,
    caption: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val shape = FloconTheme.shapes.medium

    Row(
        modifier = modifier
            .height(FloconTheme.sizes.controlHeight)
            .clip(shape)
            .background(colors.bg200)
            .border(width = 1.dp, color = colors.lineStrong, shape = shape)
            .clickable(onClick = onClick)
            .padding(horizontal = FloconTheme.spacing.compact),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        FloconIcon(
            imageVector = icon,
            modifier = Modifier.size(SelectorIconSize),
        )
        Text(
            text = caption,
            style = FloconTheme.typography.bodySmall,
            color = colors.inkFaint,
            maxLines = 1,
        )
        Text(
            text = value,
            style = FloconTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = colors.ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        FloconIcon(
            imageVector = Icons.Outlined.KeyboardArrowDown,
            modifier = Modifier.size(SelectorIconSize),
        )
    }
}

@Preview
@Composable
private fun FloconPageSelectorPreview() {
    FloconTheme {
        FloconPageSelector(
            icon = Icons.Outlined.TableChart,
            caption = "Table",
            value = "sample",
            onClick = {},
        )
    }
}
