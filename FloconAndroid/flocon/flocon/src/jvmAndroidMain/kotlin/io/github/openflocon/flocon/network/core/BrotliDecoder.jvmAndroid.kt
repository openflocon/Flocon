package io.github.openflocon.flocon.network.core

import io.github.openflocon.flocon.InternalFloconApi
import org.brotli.dec.BrotliInputStream

@InternalFloconApi
actual fun decodeBrotli(bytes: ByteArray): ByteArray? = try {
    BrotliInputStream(bytes.inputStream()).use { it.readBytes() }
} catch (_: Exception) {
    null
}
