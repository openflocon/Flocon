package io.github.openflocon.flocondesktop.main.ui.model.leftpanel

import org.jetbrains.compose.resources.DrawableResource

data class LeftPanelItem(
    val id: String,
    val icon: DrawableResource,
    val text: String,
    val isSelected: Boolean,
)
