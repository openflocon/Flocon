package io.github.openflocon.flocon

internal actual fun isFloconNoOpMarkerPresent(): Boolean = try {
    Class.forName("io.github.openflocon.flocon.noop.FloconNoOpMarker")
    true
} catch (_: ClassNotFoundException) {
    false
} catch (_: LinkageError) {
    false
}
