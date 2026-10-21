package io.github.openflocon.flocon.accessibility.model

/**
 * An accessibility event, as a screen reader would receive it.
 *
 * @param type the kind of event, e.g. `VIEW_FOCUSED`, `ANNOUNCEMENT`, `VIEW_CLICKED`
 * @param screen the screen (activity class, package...) the event comes from
 * @param bounds the bounds of the element on screen, e.g. `[0,0][100,50]`
 */
data class AccessibilityEvent(
    val type: String,
    val text: String? = null,
    val contentDescription: String? = null,
    val className: String? = null,
    val screen: String? = null,
    val bounds: String? = null,
    val extras: Map<String, String> = emptyMap(),
)
