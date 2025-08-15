package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconTextButton(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = FloconTheme.colorPalette.inverseSurface,
            contentColor = FloconTheme.colorPalette.inverseOnSurface
        ),
        content = content
    )
}
