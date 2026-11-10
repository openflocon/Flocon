package io.github.openflocon.flocondesktop.features.connect.view

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.components.FloconPixelPalette
import io.github.openflocon.library.designsystem.components.FloconPixelScene
import io.github.openflocon.library.designsystem.components.FloconPixelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

private const val GridWidth = 19
private const val GridHeight = 22
private const val FrameMs = 100L

/** One pixel of the sprite is 6 dp. */
private val PixelSize = 6.dp
internal val ConnectMascotPeekWidth: Dp = PixelSize * GridWidth
internal val ConnectMascotPeekHeight: Dp = PixelSize * GridHeight

/** From the top of the sprite to the top of its paws, the window edge is just below. */
internal val ConnectMascotPeekPawsY: Dp = PixelSize * 20

private const val LoopFrames = 36
private val Frames: ImmutableList<Int> = (0 until LoopFrames).toList().toImmutableList()
private val GlanceAside = 11..21
private val EarUp = setOf(16, 17, 29, 30)
private const val BlinkFrame = 33

/**
 * The upper body of the mascot looking at the window from above, facing left: it looks at the
 * window, glances aside (a "?" shows up), blinks and twitches an ear. 36 frames of [FrameMs].
 */
@Composable
internal fun ConnectMascotPeek(modifier: Modifier = Modifier) {
    FloconPixelScene(
        frames = Frames,
        frameMs = FrameMs,
        gridWidth = GridWidth,
        gridHeight = GridHeight,
        modifier = modifier.size(ConnectMascotPeekWidth, ConnectMascotPeekHeight),
    ) { frame ->
        val aside = frame in GlanceAside
        drawBody()
        drawHead(
            lean = if (aside) 1 else 0,
            glance = aside,
            blink = frame == BlinkFrame,
            earUp = frame in EarUp,
        )
        drawPaws()
        if (aside) drawQuestionMark()
    }
}

/** The sprite is drawn facing right on this grid (x from 13, y from -8), then flipped. */
private fun FloconPixelScope.mirrored(x: Int, y: Int, width: Int, height: Int, color: Color) {
    pixels(32 - x - width, y + 8, width, height, color)
}

private fun FloconPixelScope.drawBody() {
    mirrored(16, 7, 12, 6, FloconPixelPalette.GoldenBody)
    mirrored(19, 8, 6, 5, FloconPixelPalette.GoldenLight)
    mirrored(17, 11, 3, 3, FloconPixelPalette.GoldenDark)
    mirrored(24, 11, 3, 3, FloconPixelPalette.GoldenDark)
}

private fun FloconPixelScope.drawHead(lean: Int, glance: Boolean, blink: Boolean, earUp: Boolean) {
    val x = lean
    val y = -3
    mirrored(17 + x, 2 + y, 12, 11, FloconPixelPalette.Golden)
    mirrored(25 + x, 8 + y, 6, 5, FloconPixelPalette.GoldenLight)
    mirrored(29 + x, 8 + y, 2, 2, FloconPixelPalette.Ink)
    mirrored(21 + x, 10 + y, 2, 1, FloconPixelPalette.Blush)
    when {
        blink -> mirrored(21 + x, 7 + y, 3, 1, FloconPixelPalette.Ink)

        glance -> {
            mirrored(23 + x, 6 + y, 2, 3, FloconPixelPalette.Ink)
            mirrored(23 + x, 6 + y, 1, 1, FloconPixelPalette.White)
        }

        else -> {
            mirrored(22 + x, 5 + y, 2, 3, FloconPixelPalette.Ink)
            mirrored(22 + x, 5 + y, 1, 1, FloconPixelPalette.White)
        }
    }
    if (earUp) {
        mirrored(15 + x, 3 + y, 3, 7, FloconPixelPalette.Ear)
        mirrored(15 + x, 8 + y, 3, 2, FloconPixelPalette.Pink)
    } else {
        mirrored(15 + x, 4 + y, 3, 7, FloconPixelPalette.Ear)
        mirrored(15 + x, 9 + y, 3, 2, FloconPixelPalette.Pink)
    }
}

private fun FloconPixelScope.drawPaws() {
    mirrored(18, 12, 4, 2, FloconPixelPalette.GoldenBody)
    mirrored(24, 12, 3, 2, FloconPixelPalette.GoldenBody)
}

private fun FloconPixelScope.drawQuestionMark() {
    val color = FloconPixelPalette.Spark
    // not flipped: a "?" has to stay readable, it sits next to the muzzle
    pixels(2, 1, 3, 1, color)
    pixels(4, 2, 1, 1, color)
    pixels(3, 3, 1, 1, color)
    pixels(3, 5, 1, 1, color)
}
