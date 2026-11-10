package io.github.openflocon.flocon.accessibility.model

import kotlinx.serialization.Serializable

/**
 * @param supported false when the platform cannot capture accessibility events automatically
 * @param enabled true when an assistive technology (TalkBack, VoiceOver...) is on
 * @param canToggleTalkBack true when the app is allowed to turn TalkBack on and off (Android, WRITE_SECURE_SETTINGS granted)
 */
@Serializable
internal data class AccessibilityStateItem(
    val supported: Boolean,
    val enabled: Boolean,
    val touchExploration: Boolean,
    val services: List<String>,
    val canToggleTalkBack: Boolean = false,
)
