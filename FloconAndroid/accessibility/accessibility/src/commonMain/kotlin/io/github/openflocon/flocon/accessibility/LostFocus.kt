package io.github.openflocon.flocon.accessibility

import io.github.openflocon.flocon.accessibility.model.AccessibilityEvent

/**
 * A "focus cleared" event cannot name the element it concerns: it has already lost the focus.
 * This describes it with [lastFocused], the event of the element that had the focus, and marks it with `lostFocus`.
 * The event is returned as is when nothing is known.
 */
internal fun AccessibilityEvent.describingLostFocus(lastFocused: AccessibilityEvent?): AccessibilityEvent {
    lastFocused ?: return this
    return copy(
        text = text ?: lastFocused.text,
        contentDescription = contentDescription ?: lastFocused.contentDescription,
        className = lastFocused.className ?: className,
        bounds = lastFocused.bounds ?: bounds,
        extras = lastFocused.extras + extras + ("lostFocus" to "true"),
    )
}
