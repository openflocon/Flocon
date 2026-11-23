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
        color = FloconTheme.colors.accent
    )
}

/** A determinate bar: [progress] from 0 to 1. */
@Composable
fun FloconLinearProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier,
        color = FloconTheme.colors.accent,
        trackColor = FloconTheme.colors.line,
    )
}
