package io.github.openflocon.flocon.accessibility

/** Whether the app is allowed to turn TalkBack on and off (Android: WRITE_SECURE_SETTINGS, granted with adb). */
internal expect fun canToggleTalkBack(): Boolean

/** Turns TalkBack on or off, false when it could not be done. The other accessibility services are left as they are. */
internal expect fun setTalkBackEnabled(enabled: Boolean): Boolean
