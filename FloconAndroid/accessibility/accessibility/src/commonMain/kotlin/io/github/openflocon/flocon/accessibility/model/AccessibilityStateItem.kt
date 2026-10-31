package io.github.openflocon.flocon.accessibility.model

import kotlinx.serialization.Serializable

/**
 * @param supported false when the platform cannot capture accessibility events automatically
 * @param enabled true when an assistive technology (TalkBack, VoiceOver...) is on
 */
@Serializable
internal data class AccessibilityStateItem(
    val supported: Boolean,
    val enabled: Boolean,
    val touchExploration: Boolean,
    val services: List<String>,
)
