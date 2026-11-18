package io.github.openflocon.library.designsystem.common

import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Immutable
sealed class FloconContextMenuItem(
    label: String,
    onClick: () -> Unit
) : ContextMenuItem(label = label, onClick = onClick) {

    // [shortcut] is only displayed, the screen has to handle the keys
    @Immutable
    class Item(
        label: String,
        onClick: () -> Unit,
        val icon: ImageVector? = null,
        val shortcut: String? = null,
    ) : FloconContextMenuItem(label, onClick)

    @Immutable
    class SubMenu(
        label: String,
        val items: ImmutableList<FloconContextMenuItem>,
        val icon: ImageVector? = null,
    ) : FloconContextMenuItem(label, onClick = {})

    // a small title above a group of lines
    @Immutable
    class Header(label: String) : FloconContextMenuItem(label, onClick = {})

    // "caption / value" line, the value is a slot: a plain text, a tag...
    @Immutable
    class Detail(
        caption: String,
        onClick: () -> Unit,
        val content: @Composable () -> Unit,
    ) : FloconContextMenuItem(caption, onClick)

    @Immutable
    class Separator : FloconContextMenuItem(label = "", onClick = {})
}

class FloconContextMenuBuilder internal constructor() {
    internal val list = mutableListOf<FloconContextMenuItem>()

    fun item(label: String, onClick: () -> Unit) {
        list.add(FloconContextMenuItem.Item(label, onClick))
    }

    fun item(label: String, icon: ImageVector?, shortcut: String? = null, onClick: () -> Unit) {
        list.add(FloconContextMenuItem.Item(label, onClick, icon, shortcut))
    }

    fun subMenu(label: String, block: FloconContextMenuBuilder.() -> Unit) {
        subMenu(label = label, icon = null, block = block)
    }

    fun subMenu(label: String, icon: ImageVector?, block: FloconContextMenuBuilder.() -> Unit) {
        list.add(
            FloconContextMenuItem.SubMenu(
                label = label,
                items = buildMenu(block),
                icon = icon,
            )
        )
    }

    fun header(label: String) {
        list.add(FloconContextMenuItem.Header(label))
    }

    fun detail(caption: String, onClick: () -> Unit, content: @Composable () -> Unit) {
        list.add(FloconContextMenuItem.Detail(caption, onClick, content))
    }

    fun separator() {
        list.add(FloconContextMenuItem.Separator())
    }
}

fun buildMenu(block: FloconContextMenuBuilder.() -> Unit): ImmutableList<FloconContextMenuItem> {
    val config = FloconContextMenuBuilder()

    config.apply(block)

    return config.list.toImmutableList()
}
