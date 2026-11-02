package io.github.openflocon.library.designsystem.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

private val RemoveIconSize = 14.dp

/**
 * A rule typed by the user, that keeps ([exclude] false, green +) or hides ([exclude] true, red −) what matches [text].
 */
@Composable
fun FloconRuleChip(
    text: String,
    exclude: Boolean,
    onRemove: () -> Unit,
    removeContentDescription: String,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    Row(
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(colors.bg300)
            .border(1.dp, colors.line, FloconTheme.shapes.medium)
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Text(
            text = if (exclude) "−" else "+",
            style = FloconTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (exclude) colors.statusError else colors.statusSuccess,
        )
        Text(
            text = text,
            style = FloconTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
            color = colors.ink,
            maxLines = 1,
        )
        FloconIcon(
            imageVector = Icons.Outlined.Close,
            modifier = Modifier
                .size(RemoveIconSize)
                .semantics { contentDescription = removeContentDescription }
                .clickable(onClick = onRemove),
        )
    }
}

@Preview
@Composable
private fun FloconRuleChipPreview() {
    FloconTheme {
        Row(
            modifier = Modifier
                .background(FloconTheme.colors.bg100)
                .padding(FloconTheme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            FloconRuleChip(text = "101", exclude = false, onRemove = {}, removeContentDescription = "Remove")
            FloconRuleChip(text = "500-503", exclude = true, onRemove = {}, removeContentDescription = "Remove")
        }
    }
}
