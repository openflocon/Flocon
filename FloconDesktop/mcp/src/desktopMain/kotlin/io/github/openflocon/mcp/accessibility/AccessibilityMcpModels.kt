package io.github.openflocon.mcp.accessibility

import kotlinx.serialization.Serializable

@Serializable
internal data class AccessibilityEventsMcpResult(
    val deviceId: String,
    val packageName: String,
    /** every type of event received, to filter on */
    val types: List<String>,
    val returned: Int,
    val hasMore: Boolean,
    val events: List<AccessibilityEventMcpModel>,
)

@Serializable
internal data class AccessibilityEventMcpModel(
    val eventId: String,
    val type: String,
    val text: String? = null,
    val contentDescription: String? = null,
    val className: String? = null,
    val screen: String? = null,
    val bounds: String? = null,
    val extras: Map<String, String>,
    val timestamp: String,
    val timestampMs: Long,
)

@Serializable
internal data class AccessibilityStateMcpResult(
    val deviceId: String,
    val packageName: String,
    /** false: the device did not send its state yet, the other fields are not set */
    val known: Boolean,
    /** false: the platform cannot capture the events automatically, the app has to log them */
    val supported: Boolean? = null,
    /** true: an assistive technology (TalkBack, VoiceOver...) is running */
    val enabled: Boolean? = null,
    val touchExploration: Boolean? = null,
    val services: List<String>? = null,
)

@Serializable
internal data class ClearAccessibilityEventsMcpResult(
    val deviceId: String,
    val packageName: String,
    val deleted: Int,
)

@Serializable
internal data class AccessibilityFocusedNodeMcpResult(
    val deviceId: String,
    val packageName: String,
    /** false: no element has the accessibility focus (or no screen reader is running), the other fields are not set */
    val focused: Boolean,
    /** only for move_accessibility_focus: false when the focus did not change, ex: already on the last element */
    val moved: Boolean? = null,
    val text: String? = null,
    val contentDescription: String? = null,
    val className: String? = null,
    val screen: String? = null,
    val bounds: String? = null,
    val extras: Map<String, String>? = null,
    /** 1-based position in the focus order of the screen, when known */
    val position: Int? = null,
    /** number of elements in the focus order of the screen, when known */
    val count: Int? = null,
)
