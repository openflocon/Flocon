package io.github.openflocon.domain.accessibility.models

/**
 * The element the screen reader is on, in the focus order of the screen of the device
 */
data class AccessibilityFocusedNodeDomainModel(
    /** false: nothing has the accessibility focus */
    val found: Boolean,
    val text: String?,
    val contentDescription: String?,
    val className: String?,
    val screen: String?,
    val bounds: String?,
    val extras: Map<String, String>,
    /** Position in the focus order of the screen, only known after a request to the device */
    val index: Int?,
    val count: Int?,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
)
