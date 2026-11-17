package io.github.openflocon.flocon

// The class is looked up by name, so R8 must not rename it: the consumer rules of flocon-no-op keep it.
// When R8 does remove it, the same consumer rules already make FloconRuntime.isEnabled() return false,
// so a missing class only ever means "flocon-no-op is not in the app".
internal actual fun isFloconNoOpMarkerPresent(): Boolean = try {
    Class.forName("io.github.openflocon.flocon.noop.FloconNoOpMarker")
    true
} catch (_: ClassNotFoundException) {
    false
} catch (_: LinkageError) {
    false
}
