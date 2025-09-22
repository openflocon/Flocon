package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconTextValue(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(2.dp)
    ) {
        Text(
            text = label,
            style = FloconTheme.typography.labelSmall,
            modifier = Modifier.weight(1f)
        )
        SelectionContainer(
            modifier = Modifier.weight(1f)
                .clip(FloconTheme.shapes.small)
                .background(FloconTheme.colorPalette.primary)
                .padding(4.dp)
        ) {
            Text(
                text = value,
                style = FloconTheme.typography.labelSmall
            )
        }
    }
}
