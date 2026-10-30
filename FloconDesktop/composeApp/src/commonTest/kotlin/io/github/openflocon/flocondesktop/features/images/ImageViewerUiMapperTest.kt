package io.github.openflocon.flocondesktop.features.images

import io.github.openflocon.flocondesktop.features.images.mapper.buildImageViewer
import io.github.openflocon.flocondesktop.features.images.mapper.formatImagePosition
import io.github.openflocon.flocondesktop.features.images.model.previewImagesStateUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ImageViewerUiMapperTest {

    private val images = previewImagesStateUiModel(items = 5).images

    @Test
    fun `position text is one based`() {
        assertEquals("8 / 42", formatImagePosition(index = 7, count = 42))
    }

    @Test
    fun `middle image has both neighbours`() {
        val viewer = buildImageViewer(images, images[2].id)!!
        assertEquals(2, viewer.currentIndex)
        assertEquals("3 / 5", viewer.positionText)
        assertEquals(images[1].id, viewer.previousId)
        assertEquals(images[3].id, viewer.nextId)
    }

    @Test
    fun `first image has no previous and last has no next`() {
        assertNull(buildImageViewer(images, images.first().id)!!.previousId)
        assertNull(buildImageViewer(images, images.last().id)!!.nextId)
    }

    @Test
    fun `unknown id gives no viewer`() {
        assertNull(buildImageViewer(images, "missing"))
    }
}
