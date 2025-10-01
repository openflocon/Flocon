package io.github.openflocon.flocondesktop.features.network.mock.list.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun MocksHeaderView(modifier: Modifier = Modifier) {
    val typo = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
    val textColor = FloconTheme.colorPalette.onSurface.copy(alpha = 0.7f)

    Row(
        modifier = modifier.background(FloconTheme.colorPalette.primary.copy(alpha = 0.6f))
            .padding(vertical = 12.dp)
    ) {
        Text(
            "Enabled",
            style = typo,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp).width(50.dp),
        )
        Text(
            "Pattern",
            textAlign = TextAlign.Center,
            style = typo,
            color = textColor,
            modifier = Modifier.weight(1f),
        )
        Text(
            "Shared",
            style = typo,
            color = textColor,
        )
        Spacer(modifier = Modifier.width(32.dp))
    }
}
