package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconTableHeaderLabel
import io.github.openflocon.library.designsystem.components.floconTableHeaderTextStyle

/** The name of a group (of the sidebar or of the content) in small muted capitals, with its size when given. */
@Composable
internal fun DeeplinkSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    count: Int? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
    ) {
        FloconTableHeaderLabel(text = text, color = FloconTheme.colors.inkMuted)
        count?.let {
            Text(
                text = "· $it",
                style = floconTableHeaderTextStyle().copy(fontWeight = FontWeight.Normal),
                color = FloconTheme.colors.inkMuted,
                maxLines = 1,
            )
        }
    }
}

@Composable
@Preview
private fun DeeplinkSectionLabelPreview() {
    FloconTheme {
        DeeplinkSectionLabel(text = "Declared", count = 5)
    }
}
