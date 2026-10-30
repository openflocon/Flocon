package io.github.openflocon.flocondesktop.app.ui.view.topbar

import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class LoadGifFramesTest {

    private val gifBytes = File("src/commonMain/composeResources/files/app_icon_animated.gif").readBytes()

    @Test
    fun decodesEveryFrameWithItsDuration() = runBlocking {
        val frames = loadGifFrames(gifBytes)

        assertEquals(17, frames.size)
        assertEquals(3050L, frames.sumOf { it.durationMillis })
        assertEquals(650L, frames.first().durationMillis)
    }

    @Test
    fun framesKeepTheGifSize() = runBlocking {
        val frames = loadGifFrames(gifBytes)

        frames.forEach {
            assertEquals(144, it.image.width)
            assertEquals(144, it.image.height)
        }
    }
}
