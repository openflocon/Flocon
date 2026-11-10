package io.github.openflocon.flocondesktop.features.images.processor

import androidx.compose.ui.graphics.asComposeImageBitmap
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import coil3.size.SizeResolver
import coil3.toBitmap
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import io.github.openflocon.library.designsystem.common.saveImageToFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.path
import okio.FileSystem
import okio.Path.Companion.toPath

sealed interface DownloadImageResult {
    data class Saved(val path: String) : DownloadImageResult

    /** The user closed the save dialog. */
    data object Cancelled : DownloadImageResult

    data object Failed : DownloadImageResult
}

/**
 * Saves the original file of an image: its bytes are read from the Coil disk cache, so the format is kept.
 * When the cache has no entry, the decoded image is re-encoded as a PNG.
 */
class DownloadImageProcessor {

    suspend operator fun invoke(model: ImagesUiModel): DownloadImageResult {
        val imageLoader = SingletonImageLoader.get(PlatformContext.INSTANCE)
        val result = imageLoader.execute(buildRequest(model))
        val image = result.image ?: return DownloadImageResult.Failed

        val bytes = readOriginalBytes(imageLoader, model.url)
        if (bytes == null) {
            val saved = saveImageToFile(
                bitmap = image.toBitmap().asComposeImageBitmap(),
                defaultFileName = "${suggestedName(model.url, extension = "png").substringBeforeLast('.')}.png",
            )
            return if (saved) DownloadImageResult.Saved(path = "") else DownloadImageResult.Failed
        }

        val extension = extensionOf(model.format)
        val fileName = suggestedName(model.url, extension)
        val selected = FileKit.openFileSaver(
            suggestedName = fileName.substringBeforeLast('.'),
            defaultExtension = fileName.substringAfterLast('.', extension ?: "png"),
        ) ?: return DownloadImageResult.Cancelled

        val path = selected.path
        FileSystem.SYSTEM.write(path.toPath()) { write(bytes) }
        return DownloadImageResult.Saved(path)
    }

    private fun buildRequest(model: ImagesUiModel) = ImageRequest.Builder(PlatformContext.INSTANCE)
        .data(model.url)
        .size(SizeResolver.ORIGINAL)
        .httpHeaders(
            NetworkHeaders.Builder().apply {
                model.headers.forEach { (key, value) -> set(key, value) }
            }.build(),
        )
        .build()

    private fun readOriginalBytes(imageLoader: ImageLoader, url: String): ByteArray? {
        val diskCache = imageLoader.diskCache ?: return null
        val snapshot = diskCache.openSnapshot(url) ?: return null
        return snapshot.use { diskCache.fileSystem.read(it.data) { readByteArray() } }
    }

    private fun extensionOf(format: String?): String? = format
        ?.lowercase()
        ?.takeIf { it.isNotBlank() }
        ?.let { if (it == "jpeg") "jpg" else it }

    /** Last path segment of the url (without query) with the extension of the format when it has none. */
    private fun suggestedName(url: String, extension: String?): String {
        val name = url.substringBefore('#').substringBefore('?')
            .substringAfter("://").substringAfter('/', "")
            .substringAfterLast('/')
            .ifBlank { "image" }
        val hasExtension = name.substringAfterLast('.', "").let { it.isNotEmpty() && it.length <= 5 }
        return if (hasExtension || extension == null) name else "$name.$extension"
    }
}
