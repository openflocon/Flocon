package io.github.openflocon.flocondesktop.app.capture

import androidx.compose.ui.graphics.ImageBitmap
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.library.designsystem.common.encodeImageToPng
import io.github.openflocon.mcp.capture.FloconWindowCapture
import io.github.openflocon.mcp.capture.FloconWindowCapturer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

/**
 * Captures the Flocon main window as it is drawn, including its dialogs, menus and popups.
 */
class WindowCaptureController : FloconWindowCapturer {

    override suspend fun capture(): Either<Throwable, FloconWindowCapture> = try {
        val bitmap = withTimeoutOrNull(CAPTURE_TIMEOUT) { captureMainWindow() }
            ?: error("The Flocon window did not answer in $CAPTURE_TIMEOUT: is it displayed?")
        val image = withContext(Dispatchers.Default) { encodeImageToPng(bitmap) }
        Success(FloconWindowCapture(pngBytes = image.pngBytes, width = image.width, height = image.height))
    } catch (e: CancellationException) {
        throw e
    } catch (t: Throwable) {
        Failure(t)
    }

    private companion object {
        val CAPTURE_TIMEOUT = 5.seconds
    }
}

/** Renders what the Flocon main window displays */
internal expect suspend fun captureMainWindow(): ImageBitmap
