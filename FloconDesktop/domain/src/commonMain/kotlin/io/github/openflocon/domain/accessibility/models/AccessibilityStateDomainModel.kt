package io.github.openflocon.domain.accessibility.models

/**
 * What the device says about its assistive technologies
 */
data class AccessibilityStateDomainModel(
    /** false: the platform cannot capture the events automatically, the app has to log them */
    val supported: Boolean,
    /** true: an assistive technology (TalkBack, VoiceOver...) is running */
    val enabled: Boolean,
    val touchExploration: Boolean,
    val services: List<String>,
    /** true: the app can turn TalkBack on and off by itself, otherwise it needs a permission granted with adb first */
    val canToggleTalkBack: Boolean = false,
)
