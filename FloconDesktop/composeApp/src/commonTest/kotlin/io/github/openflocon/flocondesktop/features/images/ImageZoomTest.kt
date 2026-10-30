package io.github.openflocon.flocondesktop.features.images

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import io.github.openflocon.flocondesktop.features.images.mapper.formatImageZoom
import io.github.openflocon.flocondesktop.features.images.view.viewer.ImageScrollPanPx
import io.github.openflocon.flocondesktop.features.images.view.viewer.ImageZoomState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImageZoomTest {

    private fun state() = ImageZoomState().apply { viewportSize = IntSize(800, 600) }

    @Test
    fun `fit has no percentage`() {
        assertNull(formatImageZoom(1f))
        assertEquals("200%", formatImageZoom(2f))
        assertEquals("125%", formatImageZoom(1.25f))
    }

    @Test
    fun `scale is clamped between fit and 8x`() {
        val state = state()
        state.zoomBy(0.5f)
        assertEquals(1f, state.scale)
        state.zoomTo(100f)
        assertEquals(8f, state.scale)
    }

    @Test
    fun `zoom keeps the focused point in place`() {
        val state = state()
        val focus = Offset(100f, 50f)
        state.zoomTo(2f, focus)
        // the point that was under the focus is still displayed at the focus
        assertEquals(focus, state.offset + focus * state.scale)
        assertEquals(Offset(-100f, -50f), state.offset)
    }

    @Test
    fun `pan is clamped to the viewport`() {
        val state = state()
        state.zoomTo(2f)
        state.panBy(Offset(10_000f, -10_000f))
        assertEquals(Offset(400f, -300f), state.offset)
    }

    @Test
    fun `pan does nothing at fit and reset recenters`() {
        val state = state()
        state.panBy(Offset(50f, 50f))
        assertEquals(Offset.Zero, state.offset)
        state.zoomTo(3f, Offset(10f, 10f))
        state.reset()
        assertEquals(1f, state.scale)
        assertEquals(Offset.Zero, state.offset)
    }

    @Test
    fun `double tap toggles between fit and 2x`() {
        val state = state()
        state.toggleZoom(Offset.Zero)
        assertTrue(state.isZoomed)
        assertEquals(2f, state.scale)
        state.toggleZoom(Offset.Zero)
        assertFalse(state.isZoomed)
    }

    @Test
    fun `scroll pans opposite to the delta, clamped, and does nothing at fit`() {
        val state = state()
        state.panByScroll(Offset(0f, 5f))
        assertEquals(Offset.Zero, state.offset)
        state.zoomTo(2f)
        state.panByScroll(Offset(1f, 2f))
        assertEquals(Offset(-ImageScrollPanPx, -2f * ImageScrollPanPx), state.offset)
        state.panByScroll(Offset(-1000f, -1000f))
        assertEquals(Offset(400f, 300f), state.offset)
    }
}
