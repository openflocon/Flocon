@file:Suppress("ktlint")
package com.florent37.flocondesktop.common.ui

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
