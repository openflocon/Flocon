package io.github.openflocon.flocon.ktor

internal actual fun decodeNetworkBody(bytes: ByteArray, headers: Map<String, String>): String {
    // iOS does not have Brotli library; fallback to UTF-8 string
    return bytes.decodeToString()
}
