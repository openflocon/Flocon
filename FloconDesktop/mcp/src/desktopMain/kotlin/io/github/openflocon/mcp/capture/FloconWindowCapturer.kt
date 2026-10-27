package io.github.openflocon.mcp.capture

import io.github.openflocon.domain.common.Either

/**
 * Captures what the Flocon desktop window currently displays.
 *
 * Implemented by the UI layer, the only one knowing the window
 */
interface FloconWindowCapturer {
    suspend fun capture(): Either<Throwable, FloconWindowCapture>
}

class FloconWindowCapture(
    /** The image, encoded as a PNG */
    val pngBytes: ByteArray,
    val width: Int,
    val height: Int,
)
