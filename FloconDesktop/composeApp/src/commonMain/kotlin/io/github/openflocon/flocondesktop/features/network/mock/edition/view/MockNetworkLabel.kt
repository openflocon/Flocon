package io.github.openflocon.flocondesktop.features.network.mock.edition.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun MockNetworkLabel(label: String) {
    Text(
        label,
        modifier = Modifier.padding(start = FloconTheme.spacing.extraSmall),
        color = FloconTheme.colors.ink,
        style = FloconTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Thin,
        ),
    )
}
