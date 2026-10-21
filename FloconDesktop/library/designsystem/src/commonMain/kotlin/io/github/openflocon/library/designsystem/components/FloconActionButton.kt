package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

/**
 * The button of the actions of a screen or a dialog: [primary] is the main action (the honey accent),
 * the others are neutral.
 */
@Composable
fun FloconActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = FloconTheme.colors
    val shape = FloconTheme.shapes.medium

    Row(
        modifier = modifier
            .heightIn(min = 32.dp * FloconTheme.scale)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(shape)
            .background(if (primary) colors.accent else colors.bg200)
            .border(1.dp, if (primary) colors.accent else colors.lineStrong, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact, Alignment.CenterHorizontally),
    ) {
        CompositionLocalProvider(LocalContentColor provides if (primary) colors.accentInk else colors.ink) {
            ProvideTextStyle(FloconTheme.typography.labelLarge.copy(fontWeight = if (primary) FontWeight.SemiBold else FontWeight.Medium)) {
                content()
            }
        }
    }
}
