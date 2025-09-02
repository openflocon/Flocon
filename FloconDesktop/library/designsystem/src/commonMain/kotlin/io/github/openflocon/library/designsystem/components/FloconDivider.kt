package io.github.openflocon.library.designsystem.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.openflocon.library.designsystem.FloconTheme


@Composable
fun FloconHorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = FloconTheme.colorPalette.primary
) {
    HorizontalDivider(
        modifier = modifier,
        color = color
    )
}

@Composable
fun FloconVerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = FloconTheme.colorPalette.primary
) {
    VerticalDivider(
        modifier = modifier,
        color = color
    )
}
