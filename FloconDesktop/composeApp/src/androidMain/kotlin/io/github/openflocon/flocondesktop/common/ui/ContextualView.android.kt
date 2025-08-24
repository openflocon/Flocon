@file:Suppress("ktlint")

package io.github.openflocon.flocondesktop.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun ContextualView(
    items: List<ContextualItem>,
    modifier: Modifier,
    content: @Composable (() -> Unit),
) {
    content()
}
