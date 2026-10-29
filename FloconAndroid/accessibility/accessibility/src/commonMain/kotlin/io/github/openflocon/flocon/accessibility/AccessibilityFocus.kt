package io.github.openflocon.flocon.accessibility

import io.github.openflocon.flocon.accessibility.model.AccessibilityFocusedNodeItem

internal enum class AccessibilityFocusRequest { Current, Next, Previous, Activate }

/**
 * Index of the element to focus to answer [request], in a focus order of [count] elements.
 * [currentIndex] is -1 when no element has the focus. Null when there is nothing to focus (the order does not wrap).
 */
internal fun targetFocusIndex(request: AccessibilityFocusRequest, currentIndex: Int, count: Int): Int? {
    val target = when (request) {
        AccessibilityFocusRequest.Current, AccessibilityFocusRequest.Activate -> currentIndex
        AccessibilityFocusRequest.Next -> if (currentIndex < 0) 0 else currentIndex + 1
        AccessibilityFocusRequest.Previous -> if (currentIndex < 0) count - 1 else currentIndex - 1
    }
    return target.takeIf { it in 0 until count }
}

/**
 * Moves the accessibility focus of the screen (for [AccessibilityFocusRequest.Next] and
 * [AccessibilityFocusRequest.Previous]) and returns the element that has it.
 * [currentBounds] are the bounds of the element that last got the focus, used to find where we are in the focus order.
 */
internal expect suspend fun requestAccessibilityFocus(
    request: AccessibilityFocusRequest,
    currentBounds: String?,
): AccessibilityFocusedNodeItem
