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

    class Separator : FloconContextMenuItem(label = "", onClick = {})

}

class FloconContextMenuBuilder internal constructor() {
    internal val list = mutableListOf<FloconContextMenuItem>()

    fun item(label: String, onClick: () -> Unit) {
        list.add(FloconContextMenuItem.Item(label, onClick))
    }

    fun subMenu(label: String, block: FloconContextMenuBuilder.() -> Unit) {
        list.add(
            FloconContextMenuItem.SubMenu(
                label = label,
                items = buildMenu(block)
            )
        )
    }

    fun separator() {
        list.add(FloconContextMenuItem.Separator())
    }

}

fun buildMenu(block: FloconContextMenuBuilder.() -> Unit): List<FloconContextMenuItem> {
    val config = FloconContextMenuBuilder()

    config.apply(block)

    return config.list
}
