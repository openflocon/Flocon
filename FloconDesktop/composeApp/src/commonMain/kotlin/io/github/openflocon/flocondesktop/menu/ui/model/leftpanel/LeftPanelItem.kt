package io.github.openflocon.flocondesktop.menu.ui.model.leftpanel

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.openflocon.flocondesktop.menu.ui.model.SubScreen

@Immutable
data class LeftPanelItem(
    val screen: SubScreen,
    val icon: ImageVector,
    val text: String,
    val isSelected: Boolean,
    val isEnabled: Boolean,
)
