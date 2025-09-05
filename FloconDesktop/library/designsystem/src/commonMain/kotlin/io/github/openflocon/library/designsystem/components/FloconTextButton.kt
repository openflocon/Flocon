package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.theme.contentColorFor

@Composable
fun FloconTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = FloconTheme.colorPalette.primary,
    content: @Composable RowScope.() -> Unit
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = containerColor,
            contentColor = FloconTheme.colorPalette.contentColorFor(containerColor)
        ),
        shape = FloconTheme.shapes.medium,
        modifier = modifier,
        content = content
    )
}
