package io.github.openflocon.flocondesktop.app.capture

import androidx.compose.ui.graphics.ImageBitmap
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.library.designsystem.common.encodeImageToPng
import io.github.openflocon.mcp.capture.FloconWindowCapture
import io.github.openflocon.mcp.capture.FloconWindowCapturer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

/**
 * Bridge between the MCP server and the composition: a capture is rendered by [WindowCaptureHost], the only one
 * able to read what is displayed.
 */
class WindowCaptureController : FloconWindowCapturer {

    /** Capture requests waiting for the [WindowCaptureHost] to render them */
    internal val requests = Channel<CompletableDeferred<ImageBitmap>>(Channel.UNLIMITED)

    override suspend fun capture(): Either<Throwable, FloconWindowCapture> = try {
        val request = CompletableDeferred<ImageBitmap>()
        requests.send(request)
        // nobody renders the requests when the window is not displayed
        val bitmap = withTimeoutOrNull(CAPTURE_TIMEOUT) { request.await() }
            ?: run {
                // so the host skips it if it is displayed later
                request.cancel()
                error("The Flocon window did not answer in $CAPTURE_TIMEOUT: is it displayed?")
            }
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
