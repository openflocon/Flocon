package io.github.openflocon.flocondesktop.features.connect.view

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.openflocon.library.designsystem.components.FloconPixelPalette
import io.github.openflocon.library.designsystem.components.FloconPixelScene
import io.github.openflocon.library.designsystem.components.FloconPixelScope
import io.github.openflocon.library.designsystem.components.drawMascotDigging
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

private const val SceneWidth = 105
private const val SceneHeight = 36
private const val FrameMs = 400L
private const val FirstFoundFrame = 8

private val Night = Color(0xFF232846)
private val NightCloud = Color(0xFF3A4170)
private val GrassTop = Color(0xFF4C9A65)
private val GrassMid = Color(0xFF3A7D4F)
private val GrassDark = Color(0xFF2E6440)
private val RugDark = Color(0xFFC23A3F)

private val Frames: ImmutableList<Int> = (0..14).toList().toImmutableList()
private val DigFrames = setOf(8, 10, 12)

/**
 * The mascot sniffs the ground, digs at a rug and finds a phone hidden under it.
 * 15 frames of [FrameMs]: it sniffs (0..7), digs (8..13), then looks up with the phone found.
 */
@Composable
internal fun ConnectMascotScene(modifier: Modifier = Modifier) {
    FloconPixelScene(
        frames = Frames,
        frameMs = FrameMs,
        gridWidth = SceneWidth,
        gridHeight = SceneHeight,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(SceneWidth.toFloat() / SceneHeight),
    ) { frame ->
        drawNight()
        drawPhone(found = frame >= FirstFoundFrame, pulse = frame % 2 == 0)
        drawRug(lifted = frame in DigFrames)
        val digging = frame in DigFrames
        offset(20, 5).drawMascotDigging(
            digging = digging,
            tailUp = digging,
            headY = when {
                digging -> 6
                frame % 2 == 1 -> 5
                else -> 2
            },
        )
        when (frame) {
            in 2..7 -> drawMark(question = true)
            in FirstFoundFrame..13 -> drawMark(question = false)
        }
    }
}

private fun FloconPixelScope.drawNight() {
    pixels(0, 0, SceneWidth, SceneHeight, Night)
    listOf(6 to 4, 30 to 7, 62 to 3, 100 to 6, 72 to 5).forEach { (x, y) -> pixels(x, y, 1, 1, FloconPixelPalette.Spark) }
    pixels(10, 9, 8, 2, NightCloud)
    pixels(12, 7, 5, 2, NightCloud)
    pixels(0, 29, SceneWidth, 7, GrassMid)
    pixels(0, 29, SceneWidth, 1, GrassTop)
    pixels(0, 33, SceneWidth, 3, GrassDark)
}

private fun FloconPixelScope.drawPhone(found: Boolean, pulse: Boolean) {
    pixels(85, 17, 6, 12, FloconPixelPalette.DeviceDark)
    pixels(85, 17, 6, 1, FloconPixelPalette.Device)
    pixels(86, 18, 4, 5, FloconPixelPalette.Screen)
    if (!found) return
    pixels(86, 18, 4, 5, FloconPixelPalette.BlueLight)
    pixels(87, 19, 1, 1, FloconPixelPalette.Ink)
    pixels(89, 19, 1, 1, FloconPixelPalette.Ink)
    pixels(87, 21, 2, 1, FloconPixelPalette.Ink)
    // wifi arcs, the far one blinks
    pixels(93, 19, 1, 1, FloconPixelPalette.Blue)
    pixels(94, 20, 1, 2, FloconPixelPalette.Blue)
    pixels(93, 22, 1, 1, FloconPixelPalette.Blue)
    if (pulse) {
        pixels(96, 17, 1, 1, FloconPixelPalette.BlueLight)
        pixels(97, 18, 1, 1, FloconPixelPalette.BlueLight)
        pixels(98, 19, 1, 4, FloconPixelPalette.BlueLight)
        pixels(97, 23, 1, 1, FloconPixelPalette.BlueLight)
        pixels(96, 24, 1, 1, FloconPixelPalette.BlueLight)
    }
}

private fun FloconPixelScope.drawRug(lifted: Boolean) {
    val red = FloconPixelPalette.Red
    pixels(60, 26, 32, 3, red)
    pixels(60, 27, 32, 1, FloconPixelPalette.Folder)
    // the rug is humped over the phone
    pixels(76, 25, 16, 1, red)
    pixels(79, 24, 13, 1, red)
    pixels(82, 23, 10, 1, red)
    pixels(79, 26, 13, 1, RugDark)
    pixels(92, 26, 1, 1, RugDark)
    pixels(92, 28, 1, 1, RugDark)
    if (lifted) {
        pixels(54, 25, 6, 1, red)
        pixels(55, 26, 5, 1, red)
        pixels(56, 27, 4, 2, FloconPixelPalette.Port)
        pixels(54, 24, 3, 1, RugDark)
    } else {
        pixels(54, 26, 6, 3, red)
        pixels(54, 27, 6, 1, FloconPixelPalette.Folder)
        pixels(53, 26, 1, 1, RugDark)
        pixels(53, 28, 1, 1, RugDark)
    }
}

private fun FloconPixelScope.drawMark(question: Boolean) {
    val color = FloconPixelPalette.Spark
    if (question) {
        pixels(48, 2, 3, 1, color)
        pixels(50, 3, 1, 1, color)
        pixels(49, 4, 1, 1, color)
        pixels(49, 6, 1, 1, color)
    } else {
        pixels(49, 2, 1, 3, color)
        pixels(49, 6, 1, 1, color)
    }
}
