package io.github.openflocon.library.designsystem.components

import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Blush
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

enum class FloconMascotExpression {
    Neutral,
    Blink,

    /** ^ ^ eyes, tail up. */
    Happy,
}

enum class FloconMascotPaw {
    Down,

    /** Near front paw raised forward, to push or flip something. */
    Raised,

    /** Near front paw on a keyboard, [TypingUp] is the same paw lifted. */
    Typing,
    TypingUp,
}

/** Something the mascot holds in its mouth, drawn from the corner of the mouth at ([mouthX], [mouthY]). */
typealias FloconMascotHold = FloconPixelScope.(mouthX: Int, mouthY: Int) -> Unit

/**
 * The mascot sitting, facing right, in its own 34 x 26 grid (the paws on row 24).
 * The head can be moved by ([headX], [headY]) to tug or push what it holds.
 */
fun FloconPixelScope.drawMascotSitting(
    expression: FloconMascotExpression = FloconMascotExpression.Neutral,
    tailUp: Boolean = false,
    headX: Int = 0,
    headY: Int = 0,
    paw: FloconMascotPaw = FloconMascotPaw.Down,
    hold: FloconMascotHold? = null,
) {
    val happy = expression == FloconMascotExpression.Happy
    val tail = if (happy || tailUp) -1 else 0
    pixels(5, 24, 20, 1, Shadow)
    // tail, haunch, torso
    pixels(2, 18 + tail, 4, 2, GoldenDark)
    pixels(1, 16 + tail, 2, 3, GoldenDark)
    pixels(6, 15, 11, 9, GoldenDark)
    pixels(15, 11, 9, 13, GoldenBody)
    pixels(17, 17, 5, 7, GoldenLight)
    pixels(5, 23, 8, 2, GoldenLight)
    // near front paw
    when (paw) {
        FloconMascotPaw.Down -> pixels(20, 23, 5, 2, GoldenLight)

        FloconMascotPaw.Raised -> {
            pixels(20, 23, 5, 2, GoldenPaw)
            pixels(21, 16, 4, 4, GoldenBody)
            pixels(24, 15, 3, 3, GoldenBody)
            pixels(26, 15, 2, 3, GoldenLight)
        }

        FloconMascotPaw.Typing, FloconMascotPaw.TypingUp -> {
            val lift = if (paw == FloconMascotPaw.TypingUp) -2 else 0
            pixels(20, 23, 5, 2, GoldenPaw)
            pixels(21, 17 + lift, 4, 4, GoldenBody)
            pixels(24, 19 + lift, 3, 2, GoldenBody)
            pixels(26, 19 + lift, 2, 2, GoldenLight)
        }
    }
    // head
    pixels(17 + headX, 2 + headY, 12, 11, Golden)
    pixels(25 + headX, 8 + headY, 6, 5, GoldenLight)
    pixels(29 + headX, 8 + headY, 2, 2, Ink)
    pixels(21 + headX, 10 + headY, 2, 1, Blush)
    when (expression) {
        FloconMascotExpression.Happy -> {
            pixels(22 + headX, 7 + headY, 1, 1, Ink)
            pixels(23 + headX, 6 + headY, 1, 1, Ink)
            pixels(24 + headX, 7 + headY, 1, 1, Ink)
        }

        FloconMascotExpression.Blink -> pixels(21 + headX, 7 + headY, 3, 1, Ink)

        FloconMascotExpression.Neutral -> {
            pixels(22 + headX, 5 + headY, 2, 3, Ink)
            pixels(22 + headX, 5 + headY, 1, 1, White)
        }
    }
    if (hold != null) hold(29 + headX, 12 + headY) else pixels(27 + headX, 13 + headY, 2, 1, Tongue)
    // ear
    pixels(15 + headX, 4 + headY, 3, 7, Ear)
    pixels(15 + headX, 9 + headY, 3, 2, Pink)
}

/** The mascot walking, facing right: [stepped] swaps which legs are lifted, the body bobs with them. */
fun FloconPixelScope.drawMascotWalking(stepped: Boolean) {
    val legA = if (stepped) -2 else 0
    val legB = if (stepped) 0 else -2
    val bob = if (stepped) -1 else 0
    val tail = if (stepped) -1 else 1
    val ear = if (stepped) 1 else 0

    pixels(6, 24, 20, 1, Shadow)
    pixels(3, 9 + bob + tail, 3, 2, GoldenDark)
    pixels(1, 7 + bob + tail, 3, 3, GoldenDark)
    // far legs
    pixels(11, 17 + legB, 3, 6, GoldenDark)
    pixels(11, 22 + legB, 3, 1, GoldenPaw)
    pixels(19, 17 + legA, 3, 6, GoldenDark)
    pixels(19, 22 + legA, 3, 1, GoldenPaw)
    // body
    pixels(6, 9 + bob, 18, 9, GoldenBody)
    pixels(8, 16 + bob, 14, 2, GoldenLight)
    // near legs
    pixels(7, 17 + legA, 3, 6, GoldenBody)
    pixels(7, 22 + legA, 3, 1, GoldenLight)
    pixels(21, 17 + legB, 3, 6, GoldenBody)
    pixels(21, 22 + legB, 3, 1, GoldenLight)
    // head
    pixels(18, 3 + bob, 12, 11, Golden)
    pixels(26, 9 + bob, 6, 5, GoldenLight)
    pixels(30, 9 + bob, 2, 2, Ink)
    pixels(22, 11 + bob, 2, 1, Blush)
    pixels(23, 6 + bob, 2, 3, Ink)
    pixels(23, 6 + bob, 1, 1, White)
    // near ear
    pixels(16, 5 + bob + ear, 3, 7, Ear)
    pixels(16, 10 + bob + ear, 3, 2, Pink)
}

/**
 * The mascot standing, facing right, head lowered by [headY]: [digging] stretches the near front leg forward.
 */
fun FloconPixelScope.drawMascotDigging(
    digging: Boolean,
    tailUp: Boolean = false,
    headY: Int = 2,
) {
    val tail = if (tailUp) -1 else 1
    pixels(6, 24, 20, 1, Shadow)
    pixels(3, 9 + tail, 3, 2, GoldenDark)
    pixels(1, 7 + tail, 3, 3, GoldenDark)
    // far legs
    pixels(11, 17, 3, 6, GoldenDark)
    pixels(11, 22, 3, 1, GoldenPaw)
    pixels(19, 17, 3, 6, GoldenDark)
    pixels(19, 22, 3, 1, GoldenPaw)
    // body
    pixels(6, 9, 18, 9, GoldenBody)
    pixels(8, 16, 14, 2, GoldenLight)
    // near legs
    pixels(7, 17, 3, 6, GoldenBody)
    pixels(7, 22, 3, 1, GoldenLight)
    if (digging) {
        pixels(21, 16, 3, 4, GoldenBody)
        pixels(23, 18, 3, 3, GoldenBody)
        pixels(25, 20, 2, 2, GoldenLight)
    } else {
        pixels(21, 17, 3, 6, GoldenBody)
        pixels(21, 22, 3, 1, GoldenLight)
    }
    // head
    pixels(18, 3 + headY, 12, 11, Golden)
    pixels(26, 9 + headY, 6, 5, GoldenLight)
    pixels(30, 9 + headY, 2, 2, Ink)
    pixels(22, 11 + headY, 2, 1, Blush)
    pixels(23, 6 + headY, 2, 3, Ink)
    pixels(23, 6 + headY, 1, 1, White)
    pixels(16, 5 + headY, 3, 7, Ear)
    pixels(16, 10 + headY, 3, 2, Pink)
}
