package io.github.openflocon.flocon.ktor

import org.brotli.dec.BrotliInputStream

internal actual fun decodeNetworkBody(bytes: ByteArray, headers: Map<String, String>): String {
    return if (headers.isBrotli()) {
        BrotliInputStream(bytes.inputStream()).use { it.readBytes().toString(Charsets.UTF_8) }
    } else {
        bytes.decodeToString()
    }
}
