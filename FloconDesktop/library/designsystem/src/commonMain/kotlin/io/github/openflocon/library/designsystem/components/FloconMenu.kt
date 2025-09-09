package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

private val MenuWidth = 250.dp

@Stable
fun Modifier.menuBackground() = composed {
    Modifier
        .widthIn(min = MenuWidth)
        .width(IntrinsicSize.Max)
        .clip(FloconTheme.shapes.medium)
        .background(FloconTheme.colorPalette.primary)
        .border(
            width = 1.dp,
            color = FloconTheme.colorPalette.secondary,
            shape = FloconTheme.shapes.medium
        )
}
