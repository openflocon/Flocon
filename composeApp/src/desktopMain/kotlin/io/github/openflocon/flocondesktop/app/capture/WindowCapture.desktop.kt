package io.github.openflocon.flocondesktop.app.capture

import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skiko.SkiaLayer
import java.awt.Container
import java.awt.Window

// the skia layer draws the whole window: the content, and the dialogs / popups opened on top of it
internal actual suspend fun captureMainWindow(): ImageBitmap = withContext(Dispatchers.Main) {
    val window = Window.getWindows()
        .filterIsInstance<ComposeWindow>()
        .firstOrNull { it.isShowing }
        ?: error("The Flocon window is not displayed")
    val layer = window.findSkiaLayer() ?: error("Unable to find what draws the Flocon window")
    val screenshot = layer.screenshot() ?: error("The Flocon window has not been drawn yet")
    screenshot.asComposeImageBitmap()
}

private fun Container.findSkiaLayer(): SkiaLayer? {
    if (this is SkiaLayer) return this
    return components.firstNotNullOfOrNull { (it as? Container)?.findSkiaLayer() }
}
