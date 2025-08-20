package io.github.openflocon.flocondesktop.features.network.detail.view.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DetailLineTextView(
    label: String,
    value: String,
    labelWidth: Dp,
    modifier: Modifier = Modifier,
) {
    DetailLineView(
        labelWidth = labelWidth,
        label = label,
        modifier = modifier,
    ) {
        Text(
            text = value,
            style = FloconTheme.typography.bodyMedium, // Body text for the URL
            color = FloconTheme.colorPalette.onBackground, // Primary text color
            modifier = Modifier.weight(1f), // Takes remaining space
        )
    }
}

@Composable
fun DetailLineView(
    label: String,
    labelWidth: Dp,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = FloconTheme.typography.titleSmall, // Slightly smaller title for details
            color = FloconTheme.colorPalette.onBackground.copy(alpha = 0.7f), // Muted label color
            modifier = Modifier.width(labelWidth).padding(end = 8.dp),
        )
        SelectionContainer {
            content()
        }
    }
}

@Preview
@Composable
private fun DetailLineView_closed_Preview() {
    FloconTheme {
        DetailLineTextView(
            label = "label",
            value = "value",
            labelWidth = 100.dp,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
