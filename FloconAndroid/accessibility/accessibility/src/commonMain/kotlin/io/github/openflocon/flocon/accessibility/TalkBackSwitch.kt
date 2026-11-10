package io.github.openflocon.flocon.accessibility

/** Whether the app is allowed to turn TalkBack on and off (Android: WRITE_SECURE_SETTINGS, granted with adb). */
internal expect fun canToggleTalkBack(): Boolean

/**
 * Turns TalkBack on or off. The other accessibility services are left as they are.
 * Without the permission, the TalkBack settings are opened instead, the user toggles it there.
 */
internal expect fun setTalkBackEnabled(enabled: Boolean): TalkBackToggleResult

internal sealed interface TalkBackToggleResult {
    /** TalkBack was turned on or off */
    data object Toggled : TalkBackToggleResult

    /** The permission is missing: the TalkBack settings were opened on the device */
    data object OpenedSettings : TalkBackToggleResult

    data object Failed : TalkBackToggleResult
}
