package io.github.openflocon.flocon

actual class FloconContext(
    val appName: String,
    val packageName: String,
)

internal actual fun displayClearTextError(context: FloconContext) {
    // no op on jvm
}