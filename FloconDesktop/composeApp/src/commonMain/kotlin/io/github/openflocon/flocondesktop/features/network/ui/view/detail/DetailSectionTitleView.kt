package io.github.openflocon.flocondesktop.features.network.ui.view.detail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.common.ui.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DetailSectionTitleView(
    isExpanded: Boolean,
    title: String,
    onCopy: (() -> Unit)?,
    onToggle: (isExpanded: Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Toggle Button for Request Body
        IconButton(
            onClick = { onToggle(!isExpanded) },
            modifier = Modifier.padding(end = 4.dp), // Spacing before label
        ) {
            Text(
                text = if (isExpanded) "▼" else "▶", // Toggle icon
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f), // Takes remaining space
        )
        if (onCopy != null) {
            IconButton(
                onClick = onCopy, // Utilisation du lambda onCopy
                modifier = Modifier.padding(start = 8.dp), // Espacement avec le texte
            ) {
                Text(
                    text = "📄", // Copy icon
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CodeBlockView_expanded_Preview() {
    FloconTheme {
        DetailSectionTitleView(
            isExpanded = true,
            title = "title",
            onCopy = {},
            onToggle = {},
        )
    }
}

@Preview
@Composable
private fun CodeBlockView_closed_Preview() {
    FloconTheme {
        DetailSectionTitleView(
            isExpanded = false,
            title = "title",
            onCopy = {},
            onToggle = {},
        )
    }
}
