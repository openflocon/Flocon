package io.github.openflocon.flocon.accessibility

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.accessibility.model.AccessibilityEvent
import io.github.openflocon.flocon.accessibility.model.AccessibilityStateItem

// no reliable way to observe the accessibility events on the browser, use the manual API
internal actual fun startAccessibilityCapture(
    context: FloconContext,
    onEvent: (AccessibilityEvent) -> Unit,
    onState: (AccessibilityStateItem) -> Unit,
) {
    onState(AccessibilityStateItem(supported = false, enabled = false, touchExploration = false, services = emptyList()))
}
