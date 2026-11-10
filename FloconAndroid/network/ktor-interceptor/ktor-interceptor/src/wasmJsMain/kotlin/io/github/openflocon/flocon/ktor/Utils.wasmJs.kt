package io.github.openflocon.flocon.ktor

internal actual fun decodeNetworkBody(
    bytes: ByteArray,
    headers: Map<String, String>
): String = bytes.decodeToString()
