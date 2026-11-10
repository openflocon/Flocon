package io.github.openflocon.flocondesktop.features.images

import io.github.openflocon.flocondesktop.features.images.mapper.formatImageWeight
import io.github.openflocon.flocondesktop.features.images.mapper.imageFormat
import io.github.openflocon.flocondesktop.features.images.mapper.splitTime
import io.github.openflocon.flocondesktop.features.images.mapper.splitUrl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ImageUiMapperTest {

    @Test
    fun `format comes from the content type`() {
        assertEquals("WEBP", imageFormat("image/webp", "https://a.com/x"))
        assertEquals("SVG", imageFormat("image/svg+xml", "https://a.com/x"))
        assertEquals("JPG", imageFormat("image/jpeg; charset=binary", "https://a.com/x"))
        assertEquals("PNG", imageFormat("IMAGE/PNG", "https://a.com/x"))
    }

    @Test
    fun `format falls back on the url extension`() {
        assertEquals("GIF", imageFormat(null, "https://a.com/anim.gif?x=1"))
        assertEquals("JPG", imageFormat("application/octet-stream", "https://a.com/photo.jpeg"))
    }

    @Test
    fun `format is null when unknown`() {
        assertNull(imageFormat(null, "https://a.com/image"))
        assertNull(imageFormat(null, "https://a.com/file.unknownext"))
    }

    @Test
    fun `weight is formatted`() {
        assertEquals("512 B", formatImageWeight(512))
        assertEquals("142 KB", formatImageWeight(142 * 1024L))
        assertEquals("1.5 KB", formatImageWeight(1536))
        assertEquals("1.2 MB", formatImageWeight((1.2 * 1024 * 1024).toLong() + 1000))
    }

    @Test
    fun `url is split into host and path`() {
        assertEquals("cdn.a.com" to "/img/x.png?w=1", splitUrl("https://cdn.a.com/img/x.png?w=1"))
        assertEquals("a.com:8080" to "/x", splitUrl("http://a.com:8080/x"))
        assertEquals("a.com" to "", splitUrl("https://a.com"))
        assertEquals("a.com" to "?q=1", splitUrl("https://a.com?q=1"))
    }

    @Test
    fun `time is split`() {
        assertEquals("14:53:18" to ".868", splitTime("14:53:18.868"))
        assertEquals("14:53:18" to "", splitTime("14:53:18"))
    }
}
