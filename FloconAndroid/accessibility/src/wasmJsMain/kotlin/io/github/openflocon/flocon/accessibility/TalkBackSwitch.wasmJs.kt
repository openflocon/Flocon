package io.github.openflocon.flocon.accessibility

// there is no TalkBack on this platform
internal actual fun canToggleTalkBack(): Boolean = false

internal actual fun setTalkBackEnabled(enabled: Boolean): Boolean = false
