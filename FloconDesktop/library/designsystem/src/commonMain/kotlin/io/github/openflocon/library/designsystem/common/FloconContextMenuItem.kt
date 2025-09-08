package io.github.openflocon.library.designsystem.common

import androidx.compose.foundation.ContextMenuItem

sealed class FloconContextMenuItem(
    label: String,
    onClick: () -> Unit
) : ContextMenuItem(label = label, onClick = onClick) {

    // TODO Add icon
    class Item(label: String, onClick: () -> Unit) : FloconContextMenuItem(label, onClick)

    class SubMenu(
        label: String,
        val items: List<FloconContextMenuItem>
    ) : FloconContextMenuItem(label, onClick = {})

}
