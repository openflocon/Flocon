package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
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

/**
 * 8-bit golden retriever. Faces right, mirrored when [FloconMascotState.facingRight] is false.
 */
@Composable
fun FloconMascot(
    state: FloconMascotState,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(width = FloconMascotWidth, height = FloconMascotHeight)) {
        val unit = size.width / GridWidth
        withTransform({
            if (!state.facingRight) scale(scaleX = -1f, scaleY = 1f, pivot = Offset(size.width / 2f, 0f))
        }) {
            when (state.mode) {
                FloconMascotMode.Walk -> drawWalking(unit, state)
                FloconMascotMode.Sit -> drawSitting(unit, state, asleep = false)
                FloconMascotMode.Sleep -> drawSitting(unit, state, asleep = true)
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

private fun DrawScope.drawSitting(unit: Float, state: FloconMascotState, asleep: Boolean) {
    val tail = if (!asleep && state.tailUp) -1 else 0
    val eyesClosed = asleep || state.blinking

    pixels(unit, 5, 24, 20, 1, Shadow)

    // tail
    pixels(unit, 2, 18 + tail, 4, 2, GoldenDark)
    pixels(unit, 1, 16 + tail, 2, 3, GoldenDark)
    // haunch, torso, paws
    pixels(unit, 6, 15, 11, 9, GoldenDark)
    pixels(unit, 15, 11, 9, 13, Golden)
    pixels(unit, 17, 17, 5, 7, GoldenLight)
    pixels(unit, 5, 23, 8, 2, GoldenLight)
    pixels(unit, 20, 23, 5, 2, GoldenLight)
    // far ear
    pixels(unit, 27, 0, 3, 3, EarBlue)
    pixels(unit, 27, 2, 3, 1, GoldenDark)
    // head
    pixels(unit, 17, 2, 12, 11, Golden)
    pixels(unit, 25, 8, 6, 5, GoldenLight)
    pixels(unit, 29, 8, 2, 2, Ink)
    pixels(unit, 21, 10, 2, 1, Blush)
    if (eyesClosed) {
        pixels(unit, 21, 7, 3, 1, Ink)
    } else {
        pixels(unit, 22, 5, 2, 3, Ink)
        pixels(unit, 22, 5, 1, 1, Color.White)
        pixels(unit, 27, 13, 2, 1, Tongue)
    }
    // near ear
    pixels(unit, 15, 4, 3, 7, Golden)
    pixels(unit, 15, 9, 3, 2, EarPink)

    if (asleep) {
        pixels(unit, 31, 3, 3, 1, Sleep)
        pixels(unit, 32, 4, 1, 1, Sleep)
        pixels(unit, 31, 5, 3, 1, Sleep)
        if (state.sleepZCount >= 2) {
            pixels(unit, 30, -2, 4, 1, Sleep)
            pixels(unit, 32, -1, 1, 1, Sleep)
            pixels(unit, 31, 0, 1, 1, Sleep)
            pixels(unit, 30, 1, 4, 1, Sleep)
        }
    }
}
