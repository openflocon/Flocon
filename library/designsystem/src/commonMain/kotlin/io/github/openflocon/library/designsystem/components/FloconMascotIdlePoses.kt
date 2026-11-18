package io.github.openflocon.library.designsystem.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Blue
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Blush
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Dust
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Ear
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Golden
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.GoldenBody
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.GoldenDark
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.GoldenLight
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.GoldenPaw
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Ink
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Pink
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Shadow
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Tongue
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.White

private enum class Eyes { Open, Closed, Happy, Up }

/** Draws picture [frame] of [animation], in the same 34 x 26 grid as the other poses. */
internal fun DrawScope.drawMascotIdle(unit: Float, animation: FloconMascotIdleAnimation, frame: Int) {
    val brush = Brush(this, unit)
    when (animation) {
        FloconMascotIdleAnimation.Scratch -> brush.scratch(frame)

        FloconMascotIdleAnimation.Stretch -> brush.stretch(frame)

        FloconMascotIdleAnimation.Sniff -> brush.sniff(frame)

        FloconMascotIdleAnimation.Ball -> if (frame == 1) {
            // it leans towards the ball, which is a bit farther than the sprite
            translate(left = -3 * unit) { Brush(this, unit).ball(frame) }
        } else {
            brush.ball(frame)
        }
    }
}

private class Brush(private val scope: DrawScope, private val unit: Float) {

    fun p(x: Int, y: Int, width: Int, height: Int, color: Color) = scope.pixels(unit, x, y, width, height, color)

    fun scratch(frame: Int) {
        when (frame) {
            0 -> sit()

            1 -> sit(headY = 1) {
                p(11, 18, 6, 3, Ear)
                p(15, 15, 3, 4, Ear)
                p(16, 13, 3, 3, GoldenLight)
            }

            // the whole hind leg swings: forwards to the ear, then back
            2 -> sit(Eyes.Closed, headY = 1, ear = 1) {
                p(11, 17, 6, 3, Ear)
                p(15, 11, 3, 6, Ear)
                p(16, 8, 3, 3, GoldenLight)
                p(20, 7, 1, 2, Dust)
                p(21, 10, 1, 1, Dust)
            }

            3 -> sit(Eyes.Closed, headY = 1) {
                p(9, 18, 6, 3, Ear)
                p(12, 14, 3, 5, Ear)
                p(11, 11, 3, 3, GoldenLight)
                p(9, 10, 1, 2, Dust)
                p(8, 13, 1, 1, Dust)
            }

            else -> sit(Eyes.Happy)
        }
    }

    fun stretch(frame: Int) {
        when (frame) {
            0 -> sit(Eyes.Closed)
            1 -> sit(Eyes.Closed, headX = 1, headY = 2, reach = 4)
            2 -> stretchingMid()
            3 -> stretched(mouthOpen = false)
            4 -> stretched(mouthOpen = true)
            else -> sit(Eyes.Happy)
        }
    }

    fun sniff(frame: Int) {
        when (frame) {
            0 -> stand(legA = -2, bob = -1, tail = -1)

            1 -> stand(legB = -2, headY = 5, tail = 1)

            // the nose stays on the ground, the head goes forwards then back
            2 -> stand(headX = 2, headY = 9, tail = 1) {
                p(33, 16, 1, 1, Dust)
                p(31, 15, 1, 1, Dust)
            }

            3 -> stand(headX = -2, headY = 9, ear = 1) {
                p(30, 17, 1, 1, Dust)
                p(32, 16, 1, 1, Dust)
            }

            else -> stand(headY = -1, tail = -1)
        }
    }

    fun ball(frame: Int) {
        when (frame) {
            0 -> sit(Eyes.Up, ear = 1) { ball(29, 20) }

            1 -> stand(headY = 5, tail = -1) { ball(33, 19) }

            2 -> sit(Eyes.Happy, mouthFull = true) { ball(29, 10) }

            3 -> sit(Eyes.Up, headY = -1) {
                ball(23, -4)
                p(24, 0, 1, 1, Dust)
                p(26, 1, 1, 1, Dust)
            }

            else -> sit(Eyes.Happy) { ball(26, 20) }
        }
    }

    fun ball(x: Int, y: Int) {
        p(x + 1, y, 2, 1, Blue)
        p(x, y + 1, 4, 2, Blue)
        p(x + 1, y + 3, 2, 1, Blue)
        p(x + 1, y + 1, 1, 1, White)
    }

    /** Same as the seated mascot, with the head moved by ([headX], [headY]) and a free paw of [reach] more pixels. */
    private fun sit(
        eyes: Eyes = Eyes.Open,
        headX: Int = 0,
        headY: Int = 0,
        ear: Int = 0,
        reach: Int = 0,
        mouthFull: Boolean = false,
        over: Brush.() -> Unit = {},
    ) {
        val tail = if (eyes == Eyes.Happy) -1 else 0
        p(5, 24, 20, 1, Shadow)
        p(2, 18 + tail, 4, 2, GoldenDark)
        p(1, 16 + tail, 2, 3, GoldenDark)
        p(6, 15, 11, 9, GoldenDark)
        p(15, 11, 9, 13, GoldenBody)
        p(17, 17, 5, 7, GoldenLight)
        p(5, 23, 8, 2, GoldenLight)
        p(20, 23, 5 + reach, 2, GoldenLight)
        p(17 + headX, 2 + headY, 12, 11, Golden)
        p(25 + headX, 8 + headY, 6, 5, GoldenLight)
        p(29 + headX, 8 + headY, 2, 2, Ink)
        p(21 + headX, 10 + headY, 2, 1, Blush)
        when (eyes) {
            Eyes.Happy -> {
                p(22 + headX, 7 + headY, 1, 1, Ink)
                p(23 + headX, 6 + headY, 1, 1, Ink)
                p(24 + headX, 7 + headY, 1, 1, Ink)
            }

            Eyes.Closed -> p(21 + headX, 7 + headY, 3, 1, Ink)

            Eyes.Up -> {
                p(22 + headX, 4 + headY, 2, 3, Ink)
                p(22 + headX, 4 + headY, 1, 1, White)
            }

            Eyes.Open -> {
                p(22 + headX, 5 + headY, 2, 3, Ink)
                p(22 + headX, 5 + headY, 1, 1, White)
            }
        }
        if (!mouthFull) p(27 + headX, 13 + headY, 2, 1, Tongue)
        p(15 + headX + ear, 4 + headY, 3, 7, Ear)
        p(15 + headX + ear, 9 + headY, 3, 2, EarPink)
        over()
    }

    /** Standing on four legs, the head lowered by [headY] and moved by [headX]. */
    private fun stand(
        legA: Int = 0,
        legB: Int = 0,
        bob: Int = 0,
        tail: Int = 0,
        headX: Int = 0,
        headY: Int = 0,
        ear: Int = 0,
        over: Brush.() -> Unit = {},
    ) {
        val head = headY + bob
        p(6, 24, 20, 1, Shadow)
        p(3, 9 + bob + tail, 3, 2, GoldenDark)
        p(1, 7 + bob + tail, 3, 3, GoldenDark)
        p(11, 17 + legB, 3, 6, GoldenDark)
        p(11, 22 + legB, 3, 1, GoldenPaw)
        p(19, 17 + legA, 3, 6, GoldenDark)
        p(19, 22 + legA, 3, 1, GoldenPaw)
        p(6, 9 + bob, 18, 9, GoldenBody)
        p(8, 16 + bob, 14, 2, GoldenLight)
        p(7, 17 + legA, 3, 6, GoldenBody)
        p(7, 22 + legA, 3, 1, GoldenLight)
        p(21, 17 + legB, 3, 6, GoldenBody)
        p(21, 22 + legB, 3, 1, GoldenLight)
        p(18 + headX, 3 + head, 12, 11, Golden)
        p(26 + headX, 9 + head, 6, 5, GoldenLight)
        p(30 + headX, 9 + head, 2, 2, Ink)
        p(22 + headX, 11 + head, 2, 1, Blush)
        p(23 + headX, 6 + head, 2, 3, Ink)
        p(23 + headX, 6 + head, 1, 1, White)
        p(16 + headX, 5 + head + ear, 3, 7, Ear)
        p(16 + headX, 10 + head + ear, 3, 2, EarPink)
        over()
    }

    /** Between sitting and the full stretch: the chest goes down, the front legs reach out. */
    private fun stretchingMid() {
        p(2, 24, 30, 1, Shadow)
        p(2, 19, 4, 2, GoldenDark)
        p(1, 17, 2, 3, GoldenDark)
        p(6, 15, 11, 9, GoldenDark)
        p(5, 23, 8, 2, GoldenLight)
        p(14, 13, 8, 10, GoldenBody)
        p(18, 15, 6, 7, GoldenBody)
        p(17, 18, 5, 4, GoldenLight)
        p(21, 22, 9, 2, GoldenBody)
        p(29, 22, 3, 2, GoldenLight)
        p(20, 8, 12, 11, Golden)
        p(27, 14, 6, 5, GoldenLight)
        p(31, 14, 2, 2, Ink)
        p(23, 15, 2, 1, Blush)
        p(22, 12, 3, 1, Ink)
        p(28, 18, 2, 1, Tongue)
        p(18, 10, 3, 7, Ear)
        p(18, 15, 3, 2, EarPink)
    }

    /** Chest and head on the ground, front legs stretched forwards, the hind end stays down. */
    private fun stretched(mouthOpen: Boolean) {
        p(2, 24, 30, 1, Shadow)
        p(2, 19, 4, 2, GoldenDark)
        p(1, 17, 2, 3, GoldenDark)
        p(6, 15, 11, 9, GoldenDark)
        p(5, 23, 8, 2, GoldenLight)
        p(14, 15, 8, 8, GoldenBody)
        p(19, 17, 6, 6, GoldenBody)
        p(17, 19, 6, 3, GoldenLight)
        p(21, 22, 11, 2, GoldenBody)
        p(30, 22, 3, 2, GoldenLight)
        p(21, 13, 12, 10, Golden)
        p(28, 18, 6, 4, GoldenLight)
        p(32, 18, 2, 2, Ink)
        p(24, 19, 2, 1, Blush)
        p(23, 16, 1, 1, Ink)
        p(24, 15, 1, 1, Ink)
        p(25, 16, 1, 1, Ink)
        if (mouthOpen) {
            p(29, 20, 5, 2, Ink)
            p(30, 21, 3, 1, Tongue)
        } else {
            p(29, 21, 3, 1, Tongue)
        }
        p(19, 15, 3, 6, Ear)
        p(19, 19, 3, 2, EarPink)
    }
}

private val EarPink = Pink
