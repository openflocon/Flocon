package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
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
fun FloconLineDescription(
    label: String,
    modifier: Modifier = Modifier,
    labelWidth: Dp? = null,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = FloconTheme.typography.titleSmall,
            color = FloconTheme.colorPalette.onBackground.copy(alpha = 0.7f),
            modifier = if (labelWidth != null) {
                Modifier
                    .width(labelWidth)
                    .padding(end = 8.dp)
            } else {
                Modifier.weight(1f)
            },
        )
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(align = Alignment.Start)
        ) {
            content()
        }
    }
}

@Composable
fun FloconLineDescription(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    labelWidth: Dp? = null,
    selection: Boolean = true
) {
    FloconLineDescription(
        labelWidth = labelWidth,
        label = label,
        modifier = modifier,
    ) {
        if (selection) {
            SelectionContainer {
                Text(
                    text = value,
                    style = FloconTheme.typography.bodyMedium,
                    color = FloconTheme.colorPalette.onBackground
                )
            }
        } else {
            Text(
                text = value,
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colorPalette.onBackground
            )
        }
    }
}

@Preview
@Composable
private fun DetailLineView_closed_Preview() {
    FloconTheme {
        FloconLineDescription(
            label = "label",
            value = "value",
            labelWidth = 100.dp,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
