package io.github.openflocon.library.designsystem.components

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.theme.contentColorFor

@Composable
fun FloconIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = FloconTheme.colorPalette
        .contentColorFor(LocalContentColor.current)
        .takeOrElse { LocalContentColor.current }
) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        tint = tint,
        modifier = modifier
    )
}
