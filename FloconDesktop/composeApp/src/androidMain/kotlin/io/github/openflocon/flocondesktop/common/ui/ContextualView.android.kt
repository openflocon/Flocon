@file:Suppress("ktlint")

package io.github.openflocon.domain.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun ContextualView(
    items: List<ContextualItem>,
    onSelect: (ContextualItem) -> Unit,
    modifier: Modifier,
    content: @Composable (() -> Unit),
) {
    content()
}
