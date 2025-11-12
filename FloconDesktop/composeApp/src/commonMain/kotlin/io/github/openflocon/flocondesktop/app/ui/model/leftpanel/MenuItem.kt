package io.github.openflocon.flocondesktop.app.ui.model.leftpanel

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen

@Immutable
data class MenuItem(
    val screen: SubScreen,
    val icon: ImageVector,
    val text: String,
    val isEnabled: Boolean,
)
