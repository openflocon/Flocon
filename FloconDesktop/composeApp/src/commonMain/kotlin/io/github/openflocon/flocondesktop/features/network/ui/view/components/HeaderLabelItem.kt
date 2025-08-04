package io.github.openflocon.flocondesktop.features.network.ui.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun HeaderLabelItem(
    text: String,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
) {
    val typo = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
    val textColor = FloconTheme.colorScheme.onSurface.copy(alpha = 0.7f)

    Box(
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {
        Text(
            text = text,
            style = typo,
            textAlign = TextAlign.Center,
            color = textColor,
        )
    }
}
