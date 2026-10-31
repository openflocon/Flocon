package io.github.openflocon.library.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

private val ChipShape = RoundedCornerShape(50)
private const val EmptyChipAlpha = 0.55f

/**
 * A pill that is ticked or not, ex: a class of http status or a type of content. The values of a filter are side by side.
 * A [count] of 0 fades the chip: nothing to show for it yet, it can still be ticked.
 */
@Composable
fun FloconFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    count: Int? = null,
    monospace: Boolean = false,
    leading: (@Composable () -> Unit)? = null,
) {
    val colors = FloconTheme.colors
    val background by animateColorAsState(if (selected) colors.bg300 else colors.bg000)
    val border by animateColorAsState(if (selected) colors.lineStrong else colors.line)
    Row(
        modifier = modifier
            .then(if (count == 0) Modifier.alpha(EmptyChipAlpha) else Modifier)
            .height(FloconTheme.sizes.pageBarControlHeight)
            .clip(ChipShape)
            .background(background)
            .border(1.dp, border, ChipShape)
            .clickable(onClick = onClick)
            .padding(horizontal = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        leading?.invoke()
        Text(
            text = text,
            style = FloconTheme.typography.bodyMedium.let { if (monospace) it.copy(fontFamily = FontFamily.Monospace) else it },
            fontWeight = FontWeight.SemiBold,
            color = if (selected) colors.ink else colors.inkMuted,
            maxLines = 1,
        )
        if (count != null) {
            Text(
                text = count.toString(),
                style = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                color = colors.inkFaint,
                maxLines = 1,
            )
        }
    }
}

@Preview
@Composable
private fun FloconFilterChipPreview() {
    FloconTheme {
        Row(
            modifier = Modifier
                .background(FloconTheme.colors.bg100)
                .padding(FloconTheme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            FloconFilterChip(text = "2xx", selected = true, onClick = {}, leading = { FloconDot(color = FloconTheme.colors.statusSuccess) })
            FloconFilterChip(text = "WEBSOCKET", selected = true, onClick = {}, count = 22, monospace = true)
            FloconFilterChip(text = "IMAGE", selected = false, onClick = {}, count = 0, monospace = true)
        }
    }
}
