package io.github.openflocon.flocon.accessibility.model

import kotlinx.serialization.Serializable

/** The element the screen reader is on, [found] is false when nothing has the accessibility focus. */
@Serializable
internal data class AccessibilityFocusedNodeItem(
    val found: Boolean = false,
    val text: String? = null,
    val contentDescription: String? = null,
    val className: String? = null,
    val screen: String? = null,
    val bounds: String? = null,
    val extras: Map<String, String> = emptyMap(),
    /** Position of the element in the focus order of the screen, when known. */
    val index: Int? = null,
    /** Number of elements in the focus order of the screen, when known. */
    val count: Int? = null,
    val hasNext: Boolean = false,
    val hasPrevious: Boolean = false,
)

internal fun AccessibilityEvent.toFocusedNodeItem() = AccessibilityFocusedNodeItem(
    found = true,
    text = text,
    contentDescription = contentDescription,
    className = className,
    screen = screen,
    bounds = bounds,
    extras = extras,
)
