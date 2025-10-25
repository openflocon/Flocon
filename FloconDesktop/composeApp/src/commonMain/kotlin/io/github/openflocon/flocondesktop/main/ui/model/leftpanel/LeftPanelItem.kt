package io.github.openflocon.flocondesktop.main.ui.model.leftpanel

import androidx.compose.ui.graphics.vector.ImageVector

data class LeftPanelItem(
    val id: String,
    val icon: ImageVector,
    val text: String,
    val isSelected: Boolean,
    val isEnabled: Boolean,
)
