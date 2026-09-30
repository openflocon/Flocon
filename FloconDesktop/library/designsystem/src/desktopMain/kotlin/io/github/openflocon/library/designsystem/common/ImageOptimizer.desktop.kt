package io.github.openflocon.library.designsystem.common

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

internal object ImageOptimizer {

    private const val MAX_WIDTH = 1600
    private const val MAX_HEIGHT = 2400
    private const val MAX_PIXELS = 3_600_000 // ~14.4 MB uncompressed ARGB / TIFF limit

    fun toOptimizedBufferedImage(
        bitmap: ImageBitmap,
        maxWidth: Int = MAX_WIDTH,
        maxHeight: Int = MAX_HEIGHT,
    ): BufferedImage {
        val rawImage = bitmap.toAwtImage()
        val origW = rawImage.getWidth(null).coerceAtLeast(1)
        val origH = rawImage.getHeight(null).coerceAtLeast(1)
        val totalPixels = origW.toLong() * origH.toLong()

        val scaleByDimension = minOf(
            1.0,
            maxWidth.toDouble() / origW,
            maxHeight.toDouble() / origH,
        )

        val scaleByPixels = if (totalPixels > MAX_PIXELS) {
            kotlin.math.sqrt(MAX_PIXELS.toDouble() / totalPixels)
        } else {
            1.0
        }

        val scale = minOf(scaleByDimension, scaleByPixels)

        if (scale >= 1.0) {
            if (rawImage is BufferedImage && rawImage.type == BufferedImage.TYPE_INT_ARGB) {
                return rawImage
            }
            val bufferedImage = BufferedImage(origW, origH, BufferedImage.TYPE_INT_ARGB)
            val g2d = bufferedImage.createGraphics()
            g2d.drawImage(rawImage, 0, 0, null)
            g2d.dispose()
            return bufferedImage
        }

        val targetW = (origW * scale).toInt().coerceAtLeast(1)
        val targetH = (origH * scale).toInt().coerceAtLeast(1)

        val bufferedImage = BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB)
        val g2d = bufferedImage.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.drawImage(rawImage, 0, 0, targetW, targetH, null)
        g2d.dispose()

        return bufferedImage
    }



    fun toPngByteArray(image: BufferedImage): ByteArray {
        val baos = ByteArrayOutputStream()
        val writers = ImageIO.getImageWritersByFormatName("png")
        if (writers.hasNext()) {
            val writer = writers.next()
            val param = writer.defaultWriteParam
            if (param.canWriteCompressed()) {
                param.compressionMode = ImageWriteParam.MODE_EXPLICIT
                param.compressionQuality = 0.85f
            }
            val ios = ImageIO.createImageOutputStream(baos)
            writer.output = ios
            writer.write(null, IIOImage(image, null, null), param)
            writer.dispose()
            ios.close()
        } else {
            ImageIO.write(image, "png", baos)
        }
        return baos.toByteArray()
    }

    fun saveToFile(image: BufferedImage, targetFile: File): Boolean {
        return try {
            val writers = ImageIO.getImageWritersByFormatName("png")
            if (writers.hasNext()) {
                val writer = writers.next()
                val param = writer.defaultWriteParam
                if (param.canWriteCompressed()) {
                    param.compressionMode = ImageWriteParam.MODE_EXPLICIT
                    param.compressionQuality = 0.85f
                }
                val ios = ImageIO.createImageOutputStream(targetFile)
                writer.output = ios
                writer.write(null, IIOImage(image, null, null), param)
                writer.dispose()
                ios.close()
            } else {
                ImageIO.write(image, "png", targetFile)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
