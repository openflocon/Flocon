package io.github.openflocon.library.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
fun FloconPopup(
    alignment: Alignment,
    onDismissRequest: (() -> Unit)?,
    offset: IntOffset = IntOffset.Zero,
    content: @Composable () -> Unit
) {
    Popup(
        alignment = alignment,
        offset = offset,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
        content = content
    )
}
