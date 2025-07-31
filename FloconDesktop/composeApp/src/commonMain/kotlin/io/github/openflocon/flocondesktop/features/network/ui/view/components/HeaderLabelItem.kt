package io.github.openflocon.flocondesktop.features.network.ui.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun HeaderLabelItem(
    text: String,
    contentAlignment: Alignment = Alignment.Center,
    modifier: Modifier = Modifier,
) {
    val typo = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
    val textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

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
