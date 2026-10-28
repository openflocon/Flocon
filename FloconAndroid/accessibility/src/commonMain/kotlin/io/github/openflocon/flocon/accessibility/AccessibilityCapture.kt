package io.github.openflocon.flocon.accessibility

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.accessibility.model.AccessibilityEvent
import io.github.openflocon.flocon.accessibility.model.AccessibilityStateItem

/**
 * Starts the automatic capture of the accessibility events of the platform, when there is one.
 * [onState] is called right away, then each time the accessibility settings change.
 */
internal expect fun startAccessibilityCapture(
    context: FloconContext,
    onEvent: (AccessibilityEvent) -> Unit,
    onState: (AccessibilityStateItem) -> Unit,
)
