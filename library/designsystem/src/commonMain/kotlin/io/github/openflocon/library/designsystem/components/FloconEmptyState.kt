package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

/**
 * Centered placeholder displayed when a page has nothing to show yet.
 *
 * @param illustration optional artwork displayed above the title
 * @param action optional call to action (e.g. a "How to set up" button)
 */
@Composable
fun FloconEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    illustration: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            modifier = Modifier
                .widthIn(max = 420.dp)
                .padding(FloconTheme.spacing.extraLarge),
        ) {
            if (illustration != null) {
                illustration()
                Spacer(modifier = Modifier.height(FloconTheme.spacing.small))
            }
            Text(
                text = title,
                style = FloconTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = FloconTheme.colors.ink,
                textAlign = TextAlign.Center,
            )
            Text(
                text = description,
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colors.inkMuted,
                textAlign = TextAlign.Center,
            )
            if (action != null) {
                Spacer(modifier = Modifier.height(FloconTheme.spacing.small))
                action()
            }
        }
    }
}
