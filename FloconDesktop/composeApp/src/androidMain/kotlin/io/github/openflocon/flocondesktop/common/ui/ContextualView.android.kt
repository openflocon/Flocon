@file:Suppress("ktlint")

package io.github.openflocon.domain.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.common.ui.ContextualItem

@Composable
actual fun ContextualView(
    items: List<ContextualItem>,
    onSelect: (ContextualItem) -> Unit,
    modifier: Modifier,
    content: @Composable (() -> Unit),
) {
    content()
}
