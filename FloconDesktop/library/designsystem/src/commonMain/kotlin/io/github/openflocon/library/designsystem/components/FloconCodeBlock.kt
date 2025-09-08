package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.theme.contentColorFor
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FloconCodeBlock(
    code: String,
    modifier: Modifier = Modifier,
    containerColor: Color = FloconTheme.colorPalette.primary
) {
    Box(
        modifier = modifier
            .background(
                color = containerColor,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(8.dp),
    ) {
        CompositionLocalProvider(LocalContentColor provides FloconTheme.colorPalette.contentColorFor(containerColor)) {
            SelectionContainer {
                Text(
                    text = code,
                    style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                )
            }
        }
    }
}

@Preview
@Composable
private fun CodeBlockViewPreview() {
    FloconTheme {
        FloconCodeBlock(
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
