package io.github.openflocon.flocondesktop.features.network.ui.view.mocks

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun MockNetworkLabelView(label: String) {
    Text(
        label,
        modifier = Modifier.padding(start = 4.dp),
        color = FloconTheme.colorPalette.onSurface,
        style = FloconTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Thin,
        ),
    )
}
