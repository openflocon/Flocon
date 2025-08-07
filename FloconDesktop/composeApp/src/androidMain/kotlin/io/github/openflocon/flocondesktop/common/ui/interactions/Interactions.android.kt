package io.github.openflocon.flocondesktop.common.ui.interactions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun Modifier.hover(isHover: (Boolean) -> Unit): Modifier {
    return this
}
