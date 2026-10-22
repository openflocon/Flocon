package io.github.openflocon.flocondesktop.common.ui.interactions

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier

@Stable
@Composable
fun Modifier.hover(isHover: (Boolean) -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()

    val currentIsHover by rememberUpdatedState(isHover)
    LaunchedEffect(hovered) {
        currentIsHover(hovered)
    }

    return hoverable(interactionSource)
}
