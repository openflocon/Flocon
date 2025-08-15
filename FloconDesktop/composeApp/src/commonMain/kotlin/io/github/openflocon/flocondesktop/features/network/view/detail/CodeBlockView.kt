package io.github.openflocon.flocondesktop.features.network.view.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CodeBlockView(
    code: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
        modifier
            .background(
                color = FloconTheme.colorPalette.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
            ).padding(8.dp),
    ) {
        // Wrap the Text composable with SelectionContainer
        SelectionContainer {
            Text(
                text = code,
                color = FloconTheme.colorPalette.onSurface,
                style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            )
        }
    }
}

@Preview
@Composable
private fun CodeBlockViewPreview() {
    FloconTheme {
        CodeBlockView(
            code =
            """
                {
                   "code" : "value"
                }
            """.trimIndent(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
