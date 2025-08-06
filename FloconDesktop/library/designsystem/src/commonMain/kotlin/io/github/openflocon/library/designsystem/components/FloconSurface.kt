package io.github.openflocon.library.designsystem.components

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.theme.contentColorFor

@Composable
fun FloconSurface(
    modifier: Modifier = Modifier,
    color: Color = FloconTheme.colorPalette.surface,
    content: @Composable () -> Unit
) {
    Surface(
        color = color,
        contentColor = FloconTheme.colorPalette.contentColorFor(color),
        content = content,
        modifier = modifier
    )
}
