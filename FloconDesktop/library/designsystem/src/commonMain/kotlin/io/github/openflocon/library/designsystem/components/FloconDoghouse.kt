package io.github.openflocon.library.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Port
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Red
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.RoofDark
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.White
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Wood
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.WoodDark
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private const val SceneHeight = 32
private const val HouseWidth = 40
private const val DoorCenter = 20
private const val MascotWidth = 34

/** Where the soles of the mascot rest: the sprite is drawn from this many rows below the top. */
private const val MascotTop = 7

/** Free pixels between the house and the mascot sitting outside. */
private const val Gap = 4

private const val WalkMs = 900
private const val StepsPerWalk = 14
private const val IdleTickMs = 450L
private const val BlinkEvery = 8

private val DoghouseHeight = 38.dp

/** Width that shows the house and the mascot sitting next to it: 78 pixels of 1.2 dp. */
val FloconDoghouseWidth = 94.dp

private val MascotOutside = HouseWidth + Gap

/**
 * Left edge, in pixels, of the mascot while it walks between the outside ([walk] = 1) and the door
 * of the house ([walk] = 0): at the door it is entirely on the left of the door line, so it is clipped away.
 */
internal fun doghouseMascotLeft(walk: Float): Int {
    val door = DoorCenter - MascotWidth
    return (door + (MascotOutside - door) * walk).roundToInt()
}

/**
 * Pixels to move the scene by, 0 or negative: when the width is too short for the house, it is cut on its left
 * instead of its right, the door (and so the mascot going through it) stays visible.
 */
internal fun doghouseShift(unit: Float, widthPx: Float): Int = minOf(0, (widthPx / unit).toInt() - HouseWidth)

/** The mascot is what folds the panel, the house is what unfolds it: [xPx] is the tap, in pixels of the scene. */
internal fun doghouseTapToggles(expanded: Boolean, xPx: Float): Boolean = if (expanded) {
    xPx >= MascotOutside && xPx < MascotOutside + MascotWidth
} else {
    xPx >= 0f && xPx < HouseWidth
}

/**
 * A doghouse with the mascot next to it. The mascot sits outside when [expanded], and walks into the
 * house when it is not (and back out when it is again). Tapping the mascot sends it in, tapping the
 * house lets it out: [onToggleExpanded].
 */
@Composable
fun FloconDoghouse(
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val walk by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(durationMillis = WalkMs, easing = LinearEasing),
    )
    var tick by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(IdleTickMs)
            tick++
        }
    }

    Canvas(
        modifier = modifier
            .height(DoghouseHeight)
            .semantics {
                role = Role.Button
                this.contentDescription = contentDescription
                onClick {
                    onToggleExpanded()
                    true
                }
            }
            .pointerInput(expanded, onToggleExpanded) {
                val unit = size.height.toFloat() / SceneHeight
                detectTapGestures { tap ->
                    val shift = doghouseShift(unit, size.width.toFloat())
                    if (doghouseTapToggles(expanded, tap.x / unit - shift)) onToggleExpanded()
                }
            },
    ) {
        val unit = size.height / SceneHeight
        val shift = doghouseShift(unit, size.width)
        val houseScope = FloconPixelScope(this, unit, left = shift * unit, top = 0f)
        houseScope.drawHouse()

        val blink = tick % BlinkEvery == BlinkEvery - 1
        val current = walk
        if (current <= 0f) {
            houseScope.drawEyes(blink)
            return@Canvas
        }
        val left = doghouseMascotLeft(current)
        val mascotScope = FloconPixelScope(this, unit, left = (shift + left) * unit, top = MascotTop * unit)
        // the mascot disappears behind the door line, and comes out of it
        clipRect(left = (shift + DoorCenter) * unit) {
            withTransform({
                // it looks right going out and sitting, and left heading back to the house
                if (!expanded) scale(scaleX = -1f, scaleY = 1f, pivot = Offset((shift + left + MascotWidth / 2f) * unit, 0f))
            }) {
                if (current < 1f) {
                    mascotScope.drawMascotWalking(stepped = (current * StepsPerWalk).toInt() % 2 == 0)
                } else {
                    mascotScope.drawMascotSitting(
                        expression = if (blink) FloconMascotExpression.Blink else FloconMascotExpression.Neutral,
                        tailUp = tick % 2 == 0,
                    )
                }
            }
        }
    }
}

private fun FloconPixelScope.drawHouse() {
    // roof, a stepped triangle
    for (row in 0 until 12) {
        val width = 6 + row * 3
        pixels(DoorCenter - width / 2, 2 + row, width, 1, if (row == 11) RoofDark else Red)
    }
    // walls, with the planks
    pixels(3, 14, 34, 18, Wood)
    for (plank in 1..4) pixels(3 + plank * 6, 14, 1, 18, WoodDark)
    pixels(3, 14, 34, 1, WoodDark)
    // door, arched
    pixels(16, 20, 8, 1, Port)
    pixels(15, 21, 10, 1, Port)
    pixels(14, 22, 12, 10, Port)
}

/** Two eyes shining in the dark of the door, the mascot is inside. */
private fun FloconPixelScope.drawEyes(blink: Boolean) {
    val height = if (blink) 1 else 2
    pixels(16, 26, 2, height, White)
    pixels(22, 26, 2, height, White)
}
