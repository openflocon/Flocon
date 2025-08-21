package io.github.openflocon.library.designsystem.components

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconLinearProgressIndicator(
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        modifier = modifier,
        color = FloconTheme.colorPalette.panel
    )
}
