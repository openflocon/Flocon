package io.github.openflocon.flocon.accessibility

import io.github.openflocon.flocon.accessibility.model.AccessibilityFocusedNodeItem

// the focus can't be driven on this platform
internal actual suspend fun requestAccessibilityFocus(
    request: AccessibilityFocusRequest,
    currentBounds: String?,
): AccessibilityFocusedNodeItem = AccessibilityFocusedNodeItem()
