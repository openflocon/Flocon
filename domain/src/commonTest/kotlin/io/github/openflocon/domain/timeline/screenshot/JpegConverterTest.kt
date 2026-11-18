package io.github.openflocon.domain.timeline.screenshot

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JpegConverterTest {

    private fun png(width: Int, height: Int): File {
        val file = File.createTempFile("flocon-test", ".png").apply { deleteOnExit() }
        ImageIO.write(BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), "png", file)
        return file
    }

    private fun jpeg() = File.createTempFile("flocon-test", ".jpg").apply { deleteOnExit() }

    @Test
    fun `a tall image is scaled to 640 px high keeping its ratio`() {
        val out = jpeg()

        convertPngToJpeg(png(width = 1080, height = 2400), out)

        val image = assertNotNull(ImageIO.read(out))
        assertEquals(640, image.height)
        assertEquals(288, image.width)
    }

    @Test
    fun `an image shorter than 640 px is not enlarged`() {
        val out = jpeg()

        convertPngToJpeg(png(width = 200, height = 400), out)

        val image = assertNotNull(ImageIO.read(out))
        assertEquals(400, image.height)
        assertEquals(200, image.width)
    }

    @Test
    fun `the output is a readable jpeg`() {
        val out = jpeg()

        convertPngToJpeg(png(width = 100, height = 100), out)

        // jpeg files start with the FF D8 marker
        val header = out.readBytes().take(2).map { it.toInt() and 0xFF }
        assertEquals(listOf(0xFF, 0xD8), header)
        assertTrue(out.length() > 0)
    }
}
