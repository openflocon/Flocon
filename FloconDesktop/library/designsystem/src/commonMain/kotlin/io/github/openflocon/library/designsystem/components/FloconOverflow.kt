package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.composeunstyled.DropdownPanelAnchor
import io.github.openflocon.library.designsystem.FloconTheme

@Immutable
data class FloconOverflowItem(
    val text: String,
    val onClick: () -> Unit
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FloconOverflowItem) return false

        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        return text.hashCode()
    }
}

@Composable
fun FloconOverflow(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(close: () -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    FloconDropdownMenu(
        expanded = expanded,
        anchor = DropdownPanelAnchor.BottomEnd,
        anchorContent = {
            FloconIconButton(
                onClick = { expanded = true },
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "More actions"
                )
            }
        },
        onExpandRequest = { expanded = true },
        onDismissRequest = { expanded = false },
        modifier = modifier
    ) {
        CompositionLocalProvider(LocalContentColor provides FloconTheme.colorPalette.onPrimary) {
            content { expanded = false }
        }
    }
}
