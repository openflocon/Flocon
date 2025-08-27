package io.github.openflocon.flocondesktop.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier

@Immutable
data class ContextualItem(
    val text: String,
    val onClick: () -> Unit,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContextualItem) return false

        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int = text.hashCode()
}

@Composable
expect fun ContextualView( // right click on desktop
    items: List<ContextualItem>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
)
