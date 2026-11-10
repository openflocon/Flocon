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

private val SelectorIconSize = 18.dp
private val SelectorChevronSize = 16.dp

/**
 * Selector button of a page ([FloconPageTopBar] `selector` slot): same height and shape as the filter field,
 * a subtle 1dp border over a translucent light fill (a bit darker than the filter field), then an optional leading
 * [icon] (or a custom [leadingContent], e.g. a [FloconTintedIcon]), an optional faint [caption], the [value] in semibold and a chevron. [onClick] usually opens a dropdown menu
 * anchored on it.
 */
@Composable
fun FloconPageSelector(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    caption: String? = null,
    leadingContent: (@Composable () -> Unit)? = null,
) {
    val colors = FloconTheme.colors
    val shape = FloconTheme.shapes.medium

    Row(
        modifier = modifier
            .height(FloconTheme.sizes.pageBarControlHeight)
            .clip(shape)
            .background(colors.bg300.copy(alpha = colors.bg300.alpha * 0.55f))
            .border(width = 1.dp, color = colors.line, shape = shape)
            .clickable(onClick = onClick)
            .padding(
                // a leading tile carries its own background: keep the same inset around it as above and below,
                // and balance the end (the chevron glyph already has some inner space)
                start = if (leadingContent != null) FloconTheme.spacing.extraSmall else FloconTheme.spacing.medium,
                end = if (leadingContent != null) FloconTheme.spacing.compact else FloconTheme.spacing.medium,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        if (leadingContent != null) {
            leadingContent()
        } else if (icon != null) {
            FloconIcon(
                imageVector = icon,
                modifier = Modifier.size(SelectorIconSize),
            )
        }
        if (caption != null) {
            Text(
                text = caption,
                style = FloconTheme.typography.bodySmall,
                color = colors.inkFaint,
                maxLines = 1,
            )
        }
        Text(
            text = value,
            style = FloconTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        FloconIcon(
            imageVector = Icons.Outlined.KeyboardArrowDown,
            modifier = Modifier.size(SelectorChevronSize),
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
