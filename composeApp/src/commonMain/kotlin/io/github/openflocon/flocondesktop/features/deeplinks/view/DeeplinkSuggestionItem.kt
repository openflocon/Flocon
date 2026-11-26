package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkSuggestionUiModel
import io.github.openflocon.library.designsystem.FloconTheme

/** A suggestion of a dropdown: its label, then its value (faded) when the app gave a label. */
@Composable
internal fun DeeplinkSuggestionItem(
    suggestion: DeeplinkSuggestionUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Text(
            text = suggestion.text,
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (suggestion.label != null) {
            Text(
                text = suggestion.value,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colors.inkFaint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
