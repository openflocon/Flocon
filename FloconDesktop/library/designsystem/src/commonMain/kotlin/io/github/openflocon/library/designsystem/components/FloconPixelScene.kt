package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

/** Size of a pixel scene, in pixels: the mascot (34 x 26) with room around it for an object. */
const val FloconPixelSceneWidth = 50
const val FloconPixelSceneHeight = 32

/**
 * Draws on the pixel grid of a [FloconPixelScene]: x to the right, y down, in pixels.
 */
class FloconPixelScope internal constructor(
    private val drawScope: DrawScope,
    private val unit: Float,
    private val left: Float,
    private val top: Float,
    private val originX: Int = 0,
    private val originY: Int = 0,
) {
    fun pixels(x: Int, y: Int, width: Int, height: Int, color: Color) {
        val gridX = (originX + x).coerceAtLeast(0)
        val gridY = (originY + y).coerceAtLeast(0)
        val right = (originX + x + width).coerceAtMost(FloconPixelSceneWidth)
        val bottom = (originY + y + height).coerceAtMost(FloconPixelSceneHeight)
        if (right <= gridX || bottom <= gridY) return
        val l = floor(left + gridX * unit)
        val t = floor(top + gridY * unit)
        drawScope.drawRect(
            color = color,
            topLeft = Offset(l, t),
            size = Size(ceil(left + right * unit) - l, ceil(top + bottom * unit) - t),
        )
    }

    /** Same grid, with its origin moved by ([dx], [dy]). */
    fun offset(dx: Int, dy: Int): FloconPixelScope = FloconPixelScope(drawScope, unit, left, top, originX + dx, originY + dy)
}

/**
 * Small looping 8-bit animation on a [FloconPixelSceneWidth] x [FloconPixelSceneHeight] grid,
 * scaled to fit and centered. [draw] gets the current frame: [frames] are played in a loop, [frameMs] each.
 */
@Composable
fun FloconPixelScene(
    frames: ImmutableList<Int>,
    frameMs: Long,
    modifier: Modifier = Modifier,
    draw: FloconPixelScope.(frame: Int) -> Unit,
) {
    var step by remember(frames) { mutableIntStateOf(0) }
    LaunchedEffect(frames, frameMs) {
        while (true) {
            delay(frameMs)
            step = (step + 1) % frames.size
        }
    }
    Canvas(modifier = modifier) {
        val unit = min(size.width / FloconPixelSceneWidth, size.height / FloconPixelSceneHeight)
        val left = (size.width - unit * FloconPixelSceneWidth) / 2f
        val top = (size.height - unit * FloconPixelSceneHeight) / 2f
        FloconPixelScope(this, unit, left, top).draw(frames[step])
    }
}
