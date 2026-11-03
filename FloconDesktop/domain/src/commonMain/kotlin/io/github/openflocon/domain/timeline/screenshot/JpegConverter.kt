package io.github.openflocon.domain.timeline.screenshot

import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

/**
 * Writes [png] as a jpeg at [jpeg], [maxHeightPx] high at most (the ratio is kept, a smaller image is not enlarged).
 */
fun convertPngToJpeg(
    png: File,
    jpeg: File,
    maxHeightPx: Int = 640,
    quality: Float = 0.6f,
) {
    val source = requireNotNull(ImageIO.read(png)) { "Not an image: ${png.name}" }
    val height = minOf(source.height, maxHeightPx)
    val width = (source.width.toLong() * height / source.height).toInt().coerceAtLeast(1)

    // the jpeg has no alpha: draw into an rgb image
    val target = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics = target.createGraphics()
    try {
        graphics.drawImage(source.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null)
    } finally {
        graphics.dispose()
    }

    val writer = ImageIO.getImageWritersByFormatName("jpeg").next()
    try {
        val params = writer.defaultWriteParam.apply {
            compressionMode = ImageWriteParam.MODE_EXPLICIT
            compressionQuality = quality
        }
        ImageIO.createImageOutputStream(jpeg).use { output ->
            writer.output = output
            writer.write(null, IIOImage(target, null, null), params)
        }
    } finally {
        writer.dispose()
    }
}
