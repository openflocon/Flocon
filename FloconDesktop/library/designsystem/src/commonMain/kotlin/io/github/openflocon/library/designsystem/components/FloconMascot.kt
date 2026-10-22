package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.floor

/** Size of the sprite: a 34 x 26 pixel grid, 1.5 dp per pixel. */
val FloconMascotWidth: Dp = 51.dp
val FloconMascotHeight: Dp = 39.dp

/** Distance from the top of the sprite to the soles of its paws. */
val FloconMascotFeetY: Dp = 36.dp

private const val GridWidth = 34f

private val Golden = Color(0xFFF2B544)
private val GoldenDark = Color(0xFFD99522)
private val GoldenLight = Color(0xFFF9D98B)
private val GoldenPaw = Color(0xFFEBC06A)
private val Ink = Color(0xFF2B2140)
private val EarPink = Color(0xFFFF5C9A)
private val EarBlue = Color(0xFF3AA0FF)
private val Blush = Color(0xFFFF8FB1)
private val Tongue = Color(0xFFFF7A9C)
private val Sleep = Color(0xFFF5D68B)
private val Shadow = Color(0x40000000)
private val Dust = Color(0x66FFFFFF)

/**
 * 8-bit golden retriever. Faces right, mirrored when [FloconMascotState.facingRight] is false.
 * Clicking it makes it happy ([FloconMascotState.pet]).
 */
@Composable
fun FloconMascot(
    state: FloconMascotState,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .size(width = FloconMascotWidth, height = FloconMascotHeight)
            .pointerHoverIcon(PointerIcon.Hand)
            .pointerInput(state) { detectTapGestures { state.pet() } },
    ) {
        val unit = size.width / GridWidth
        withTransform({
            if (!state.facingRight) scale(scaleX = -1f, scaleY = 1f, pivot = Offset(size.width / 2f, 0f))
        }) {
            when (state.mode) {
                FloconMascotMode.Walk -> if (state.running) drawRunning(unit, state) else drawWalking(unit, state)
                FloconMascotMode.Sit -> drawSitting(unit, state, FloconMascotMode.Sit)
                FloconMascotMode.Sleep -> drawSitting(unit, state, FloconMascotMode.Sleep)
                FloconMascotMode.Happy -> drawSitting(unit, state, FloconMascotMode.Happy)
            }
        }
    }
}

private fun DrawScope.pixels(
    unit: Float,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    color: Color,
) {
    val left = floor(x * unit)
    val top = floor(y * unit)
    drawRect(
        color = color,
        topLeft = Offset(left, top),
        size = Size(ceil((x + width) * unit) - left, ceil((y + height) * unit) - top),
    )
}

private fun DrawScope.drawWalking(unit: Float, state: FloconMascotState) {
    val lifted = state.alternateLegs
    val legA = if (lifted) -2 else 0
    val legB = if (lifted) 0 else -2
    val bob = if (lifted) -1 else 0
    val tail = if (lifted) -1 else 1
    val ear = if (lifted) 1 else 0

    pixels(unit, 6, 24, 20, 1, Shadow)

    // tail
    pixels(unit, 3, 9 + bob + tail, 3, 2, GoldenDark)
    pixels(unit, 1, 7 + bob + tail, 3, 3, GoldenDark)
    // far legs
    pixels(unit, 11, 17 + legB, 3, 6, GoldenDark)
    pixels(unit, 11, 22 + legB, 3, 1, GoldenPaw)
    pixels(unit, 19, 17 + legA, 3, 6, GoldenDark)
    pixels(unit, 19, 22 + legA, 3, 1, GoldenPaw)
    // body
    pixels(unit, 6, 9 + bob, 18, 9, Golden)
    pixels(unit, 8, 16 + bob, 14, 2, GoldenLight)
    // near legs
    pixels(unit, 7, 17 + legA, 3, 6, Golden)
    pixels(unit, 7, 22 + legA, 3, 1, GoldenLight)
    pixels(unit, 21, 17 + legB, 3, 6, Golden)
    pixels(unit, 21, 22 + legB, 3, 1, GoldenLight)
    // far ear
    pixels(unit, 27, 1 + bob, 3, 3, GoldenDark)
    pixels(unit, 27, bob, 3, 2, EarBlue)
    // head
    pixels(unit, 18, 3 + bob, 12, 11, Golden)
    pixels(unit, 26, 9 + bob, 6, 5, GoldenLight)
    pixels(unit, 30, 9 + bob, 2, 2, Ink)
    pixels(unit, 22, 11 + bob, 2, 1, Blush)
    if (state.blinking) {
        pixels(unit, 22, 8 + bob, 3, 1, Ink)
    } else {
        pixels(unit, 23, 6 + bob, 2, 3, Ink)
        pixels(unit, 23, 6 + bob, 1, 1, Color.White)
    }
    // near ear
    pixels(unit, 16, 5 + bob + ear, 3, 7, Golden)
    pixels(unit, 16, 10 + bob + ear, 3, 2, EarPink)
}

/** Gallop: stretched out in the air, then gathered when landing, with ears and tail streaming back. */
private fun DrawScope.drawRunning(unit: Float, state: FloconMascotState) {
    val stretched = state.alternateLegs
    val air = if (stretched) -2 else 0
    val dy = air + if (stretched) 1 else -1

    if (stretched) {
        pixels(unit, 9, 24, 14, 1, Shadow)
    } else {
        pixels(unit, 6, 24, 20, 1, Shadow)
        pixels(unit, 0, 22, 3, 2, Dust)
        pixels(unit, 3, 23, 2, 1, Dust)
    }

    // tail streaming back
    pixels(unit, 0, (if (stretched) 10 else 8) + air, 6, 2, GoldenDark)
    if (stretched) {
        // far legs reaching out, back and front
        pixels(unit, 1, 16 + air, 6, 3, GoldenDark)
        pixels(unit, 22, 16 + air, 6, 3, GoldenDark)
        // body, flat
        pixels(unit, 6, 10 + air, 18, 8, Golden)
        pixels(unit, 8, 16 + air, 14, 2, GoldenLight)
        // near legs reaching out
        pixels(unit, 3, 18 + air, 6, 3, Golden)
        pixels(unit, 2, 18 + air, 1, 3, GoldenLight)
        pixels(unit, 24, 18 + air, 6, 3, Golden)
        pixels(unit, 29, 18 + air, 1, 3, GoldenLight)
    } else {
        // legs gathered under the body
        pixels(unit, 12, 19, 3, 3, GoldenDark)
        pixels(unit, 20, 19, 3, 3, GoldenDark)
        pixels(unit, 6, 8, 18, 9, Golden)
        pixels(unit, 8, 15, 14, 2, GoldenLight)
        pixels(unit, 9, 17, 4, 5, Golden)
        pixels(unit, 9, 21, 4, 1, GoldenLight)
        pixels(unit, 17, 17, 4, 5, Golden)
        pixels(unit, 17, 21, 4, 1, GoldenLight)
    }
    // far ear streaming back
    pixels(unit, 22, 1 + dy, 5, 2, GoldenDark)
    pixels(unit, 22, 1 + dy, 2, 2, EarBlue)
    // head
    pixels(unit, 18, 3 + dy, 12, 11, Golden)
    pixels(unit, 26, 9 + dy, 6, 5, GoldenLight)
    pixels(unit, 30, 9 + dy, 2, 2, Ink)
    pixels(unit, 27, 14 + dy, 2, 2, Tongue)
    pixels(unit, 22, 11 + dy, 2, 1, Blush)
    pixels(unit, 23, 6 + dy, 2, 2, Ink)
    pixels(unit, 23, 6 + dy, 1, 1, Color.White)
    // near ear streaming back
    pixels(unit, 13, 5 + dy, 6, 3, Golden)
    pixels(unit, 13, 5 + dy, 2, 3, EarPink)
}

private fun DrawScope.drawSitting(unit: Float, state: FloconMascotState, mode: FloconMascotMode) {
    val asleep = mode == FloconMascotMode.Sleep
    val happy = mode == FloconMascotMode.Happy
    val lift = if (happy && state.hopUp) -3 else 0
    val tail = if (!asleep && (state.tailUp || happy)) -1 else 0
    val eyesClosed = asleep || state.blinking

    pixels(unit, 5, 24, 20, 1, Shadow)

    // tail
    pixels(unit, 2, 18 + tail + lift, 4, 2, GoldenDark)
    pixels(unit, 1, 16 + tail + lift, 2, 3, GoldenDark)
    // haunch, torso, paws
    pixels(unit, 6, 15 + lift, 11, 9, GoldenDark)
    pixels(unit, 15, 11 + lift, 9, 13, Golden)
    pixels(unit, 17, 17 + lift, 5, 7, GoldenLight)
    pixels(unit, 5, 23 + lift, 8, 2, GoldenLight)
    pixels(unit, 20, 23 + lift, 5, 2, GoldenLight)
    // far ear
    pixels(unit, 27, 0 + lift, 3, 3, EarBlue)
    pixels(unit, 27, 2 + lift, 3, 1, GoldenDark)
    // head
    pixels(unit, 17, 2 + lift, 12, 11, Golden)
    pixels(unit, 25, 8 + lift, 6, 5, GoldenLight)
    pixels(unit, 29, 8 + lift, 2, 2, Ink)
    pixels(unit, 21, 10 + lift, 2, 1, Blush)
    if (happy) {
        // ^ ^ eyes and the tongue out
        pixels(unit, 22, 7 + lift, 1, 1, Ink)
        pixels(unit, 23, 6 + lift, 1, 1, Ink)
        pixels(unit, 24, 7 + lift, 1, 1, Ink)
        pixels(unit, 27, 13 + lift, 2, 1, Tongue)
    } else if (eyesClosed) {
        pixels(unit, 21, 7 + lift, 3, 1, Ink)
    } else {
        pixels(unit, 22, 5 + lift, 2, 3, Ink)
        pixels(unit, 22, 5 + lift, 1, 1, Color.White)
        pixels(unit, 27, 13 + lift, 2, 1, Tongue)
    }
    // near ear
    pixels(unit, 15, 4 + lift, 3, 7, Golden)
    pixels(unit, 15, 9 + lift, 3, 2, EarPink)

    if (asleep) {
        pixels(unit, 31, 3 + lift, 3, 1, Sleep)
        pixels(unit, 32, 4 + lift, 1, 1, Sleep)
        pixels(unit, 31, 5 + lift, 3, 1, Sleep)
        if (state.sleepZCount >= 2) {
            pixels(unit, 30, -2 + lift, 4, 1, Sleep)
            pixels(unit, 32, -1 + lift, 1, 1, Sleep)
            pixels(unit, 31, 0 + lift, 1, 1, Sleep)
            pixels(unit, 30, 1 + lift, 4, 1, Sleep)
        }
    }

    if (happy) {
        drawHeart(unit, 30, -2 - state.heartFrame)
        drawHeart(unit, 24, -6 - state.heartFrame)
    }
}

private fun DrawScope.drawHeart(unit: Float, x: Int, y: Int) {
    pixels(unit, x + 1, y, 1, 1, EarPink)
    pixels(unit, x + 3, y, 1, 1, EarPink)
    pixels(unit, x, y + 1, 5, 1, EarPink)
    pixels(unit, x + 1, y + 2, 3, 1, EarPink)
    pixels(unit, x + 2, y + 3, 1, 1, EarPink)
}
