package io.github.openflocon.flocon.accessibility

// VoiceOver cannot be driven by an app
internal actual fun canToggleTalkBack(): Boolean = false

internal actual fun setTalkBackEnabled(enabled: Boolean): Boolean = false
