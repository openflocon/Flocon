package io.github.openflocon.flocondesktop.features.proxy.view

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyMoodUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconPixelPalette
import io.github.openflocon.library.designsystem.components.FloconPixelScene
import io.github.openflocon.library.designsystem.components.FloconPixelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

// same grid as the mascot of the Connect dialog, so that both sit the same way on their window
private const val GridWidth = 19
private const val GridHeight = 22
private const val FrameMs = 100L

/** One pixel of the sprite is 6 dp. */
private val PixelSize = 6.dp
private val ProxyMascotPeekWidth: Dp = PixelSize * GridWidth
private val ProxyMascotPeekHeight: Dp = PixelSize * GridHeight

/** From the top of the sprite to the top of its paws, the window edge is just below. */
internal val ProxyMascotPeekPawsY: Dp = PixelSize * 20

private enum class Beat { Still, Glint1, Glint2, Glint3, EarDown, Glance }

/**
 * The idle loop while the proxy runs, about 8.5 s: still, a glint sweeps the shades, still,
 * the ear twitches, still, a glance aside.
 */
private val WatchingLoop: List<Beat> = buildList {
    repeat(22) { add(Beat.Still) }
    add(Beat.Glint1)
    add(Beat.Glint2)
    add(Beat.Glint3)
    repeat(18) { add(Beat.Still) }
    repeat(3) { add(Beat.EarDown) }
    repeat(16) { add(Beat.Still) }
    repeat(9) { add(Beat.Glance) }
    repeat(14) { add(Beat.Still) }
}
private val WatchingFrames: ImmutableList<Int> = WatchingLoop.indices.toList().toImmutableList()

/** The sleeping loop, 4.8 s: two slow breaths (the head rises and sinks), a "z" floating up, an ear twitch. */
private const val SleepingLoopFrames = 48
private const val BreathFrames = 24
private val EarTwitch = 40..42
private val SleepingFrames: ImmutableList<Int> = (0 until SleepingLoopFrames).toList().toImmutableList()
private val StillFrame: ImmutableList<Int> = persistentListOf(0)

/**
 * The upper body of the mascot looking at the proxy dialog from above, facing left, in a long
 * black coat and dark shades that catch the code. It watches (and moves now and then) while the
 * proxy runs, sleeps with the shades up when it is off, and its shades slip on a failure.
 */
@Composable
internal fun ProxyMascotPeek(
    mood: ProxyMoodUiModel,
    modifier: Modifier = Modifier,
) {
    FloconPixelScene(
        frames = when (mood) {
            ProxyMoodUiModel.Watching -> WatchingFrames
            ProxyMoodUiModel.Asleep -> SleepingFrames
            ProxyMoodUiModel.Glitched -> StillFrame
        },
        frameMs = FrameMs,
        gridWidth = GridWidth,
        gridHeight = GridHeight,
        modifier = modifier.size(ProxyMascotPeekWidth, ProxyMascotPeekHeight),
    ) { frame ->
        val beat = if (mood == ProxyMoodUiModel.Watching) WatchingLoop[frame] else Beat.Still
        // asleep, the head sinks by one pixel on the out-breath
        val breathOut = mood == ProxyMoodUiModel.Asleep && frame % BreathFrames >= BreathFrames / 2
        val head = offset(if (beat == Beat.Glance) 1 else 0, if (breathOut) 1 else 0)
        drawBody()
        head.drawHead(
            earUp = when (mood) {
                ProxyMoodUiModel.Watching -> beat != Beat.EarDown
                ProxyMoodUiModel.Asleep -> frame in EarTwitch
                ProxyMoodUiModel.Glitched -> false
            },
        )
        // before the coat (asleep: not the chosen one yet), an office shirt and tie
        if (mood == ProxyMoodUiModel.Asleep) drawShirt() else drawCoat()
        when (mood) {
            ProxyMoodUiModel.Watching -> head.drawShades(
                top = 10,
                glint = FloconPixelPalette.Neon,
                glintDark = FloconPixelPalette.NeonDark,
                sweepX = when (beat) {
                    Beat.Glint1 -> 5
                    Beat.Glint2 -> 8
                    Beat.Glint3 -> 11
                    else -> null
                },
            )

            ProxyMoodUiModel.Asleep -> {
                // eye closed, no shades yet, a "z" floating up from the head
                head.mirrored(21, 4, 3, 1, FloconPixelPalette.Ink)
                drawSleepZ(frame)
            }

            ProxyMoodUiModel.Glitched -> {
                // the eye looks up over the shades that slipped down the muzzle
                head.mirrored(22, 1, 2, 2, FloconPixelPalette.Ink)
                head.mirrored(22, 1, 1, 1, FloconPixelPalette.White)
                head.drawShades(top = 12, glint = FloconPixelPalette.Red, glintDark = FloconPixelPalette.RedDark)
            }
        }
        drawPaws()
    }
}

/** Drawn facing right on this grid (x from 13, y from -8), then flipped: same as the Connect mascot. */
private fun FloconPixelScope.mirrored(x: Int, y: Int, width: Int, height: Int, color: Color) {
    pixels(32 - x - width, y + 8, width, height, color)
}

private fun FloconPixelScope.drawBody() {
    mirrored(16, 7, 12, 6, FloconPixelPalette.GoldenBody)
    mirrored(19, 8, 6, 5, FloconPixelPalette.GoldenLight)
    mirrored(17, 11, 3, 3, FloconPixelPalette.GoldenDark)
    mirrored(24, 11, 3, 3, FloconPixelPalette.GoldenDark)
}

private fun FloconPixelScope.drawHead(earUp: Boolean) {
    mirrored(17, -1, 12, 11, FloconPixelPalette.Golden)
    mirrored(25, 5, 6, 5, FloconPixelPalette.GoldenLight)
    mirrored(29, 5, 2, 2, FloconPixelPalette.Ink)
    mirrored(21, 7, 2, 1, FloconPixelPalette.Blush)
    val earY = if (earUp) 0 else 2
    mirrored(15, earY, 3, 7, FloconPixelPalette.Ear)
    mirrored(15, earY + 5, 3, 2, FloconPixelPalette.Pink)
}

/** A white office shirt and a dark tie, between the chin and the paws. */
private fun FloconPixelScope.drawShirt() {
    pixels(3, 18, 12, 2, FloconPixelPalette.Shirt)
    pixels(3, 18, 1, 2, FloconPixelPalette.ShirtShade)
    pixels(14, 18, 1, 2, FloconPixelPalette.ShirtShade)
    pixels(8, 17, 3, 1, FloconPixelPalette.Shirt)
    pixels(9, 18, 1, 2, FloconPixelPalette.Ink)
}

/** A small "z" rising above the head over the loop, then the next one. */
private fun FloconPixelScope.drawSleepZ(frame: Int) {
    val step = (frame % BreathFrames) / 6 // 0..3: the z climbs
    val x = 2 + step
    val y = 3 - step
    val color = if (step == 3) FloconPixelPalette.Sleep.copy(alpha = 0.5f) else FloconPixelPalette.Sleep
    // 4 x 4, so that the diagonal reads as a "z"
    pixels(x, y, 4, 1, color)
    pixels(x + 2, y + 1, 1, 1, color)
    pixels(x + 1, y + 2, 1, 1, color)
    pixels(x, y + 3, 4, 1, color)
}

/** A long black coat with a raised collar, between the chin and the paws. */
private fun FloconPixelScope.drawCoat() {
    pixels(3, 18, 12, 2, FloconPixelPalette.Coat)
    pixels(12, 16, 3, 2, FloconPixelPalette.Coat)
    pixels(11, 17, 1, 1, FloconPixelPalette.CoatLight)
    pixels(6, 18, 1, 2, FloconPixelPalette.CoatLight)
    pixels(10, 18, 1, 2, FloconPixelPalette.CoatLight)
}

/**
 * Dark shades from the side: a 2 px band with both lenses below it, a glint on each lens,
 * and a white glint at [sweepX] when one sweeps along them.
 */
private fun FloconPixelScope.drawShades(top: Int, glint: Color, glintDark: Color, sweepX: Int? = null) {
    pixels(3, top, 10, 2, FloconPixelPalette.Shades)
    pixels(4, top + 2, 3, 1, FloconPixelPalette.Shades)
    pixels(8, top + 2, 3, 1, FloconPixelPalette.Shades)
    pixels(13, top, 1, 1, FloconPixelPalette.Shades)
    pixels(4, top, 1, 1, glint)
    pixels(5, top + 1, 1, 1, glintDark)
    pixels(9, top, 1, 1, glint)
    pixels(10, top + 1, 1, 1, glintDark)
    if (sweepX != null) pixels(sweepX, top, 1, 1, FloconPixelPalette.White)
}

private fun FloconPixelScope.drawPaws() {
    mirrored(18, 12, 4, 2, FloconPixelPalette.GoldenBody)
    mirrored(24, 12, 3, 2, FloconPixelPalette.GoldenBody)
}

@Preview
@Composable
private fun ProxyMascotPeekPreview() {
    FloconTheme {
        ProxyMascotPeek(mood = ProxyMoodUiModel.Watching)
    }
}
