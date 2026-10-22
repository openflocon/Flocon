package io.github.openflocon.flocondesktop.app.ui.mascot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.components.FloconMascot
import io.github.openflocon.library.designsystem.components.FloconMascotConfig
import io.github.openflocon.library.designsystem.components.FloconMascotFeetY
import io.github.openflocon.library.designsystem.components.FloconMascotPlatform
import io.github.openflocon.library.designsystem.components.FloconMascotState
import io.github.openflocon.library.designsystem.components.FloconMascotWidth
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.roundToInt

private val StopDistance = 26.dp
private val WakeDistance = 90.dp
private val Speed = 70.dp
private val RunDistance = 320.dp
private val RunSpeed = 200.dp
private val JumpHeight = 28.dp

private const val MaxFrameMs = 50L
private const val NanosPerMilli = 1_000_000L

/**
 * Mascot walking on the [platforms], after the pointer ([pointerX], null until it has moved), and
 * jumping from one platform to another when the pointer is over the other one. Everything is in px,
 * in the coordinates of this composable.
 */
@Composable
fun MascotOverlay(
    pointerX: Float?,
    platforms: ImmutableList<FloconMascotPlatform>,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val state = remember(density) {
        FloconMascotState(
            startX = platforms.last().let { (it.minX + it.maxX) / 2f },
            config = with(density) {
                FloconMascotConfig(
                    stopDistance = StopDistance.toPx(),
                    wakeDistance = WakeDistance.toPx(),
                    speedPerSecond = Speed.toPx(),
                    runDistance = RunDistance.toPx(),
                    runSpeedPerSecond = RunSpeed.toPx(),
                    jumpHeight = JumpHeight.toPx(),
                )
            },
        )
    }
    val currentPointerX by rememberUpdatedState(pointerX)
    val currentPlatforms by rememberUpdatedState(platforms)

    LaunchedEffect(state) {
        var lastNanos = withFrameNanos { it }
        while (true) {
            withFrameNanos { nanos ->
                val deltaMs = ((nanos - lastNanos) / NanosPerMilli).coerceAtMost(MaxFrameMs)
                lastNanos = nanos
                state.update(
                    deltaMs = deltaMs,
                    timeMs = nanos / NanosPerMilli,
                    targetX = currentPointerX ?: state.x,
                    platforms = currentPlatforms,
                )
            }
        }
    }

    val halfWidth = with(density) { FloconMascotWidth.toPx() / 2f }
    val feetY = with(density) { FloconMascotFeetY.toPx() }

    Box(modifier = modifier.fillMaxSize()) {
        FloconMascot(
            state = state,
            modifier = Modifier.offset {
                IntOffset(
                    x = (state.x - halfWidth).roundToInt(),
                    y = (state.y - feetY).roundToInt(),
                )
            },
        )
    }
}

/**
 * A platform for the mascot along the top edge of an island that spans [left] to [right], keeping
 * [inset] away from its ends so the mascot never hangs over the gap between islands. An island too
 * narrow for that (a collapsed sidebar) gets a single spot in its middle.
 */
fun mascotPlatform(left: Float, right: Float, top: Float, inset: Float): FloconMascotPlatform {
    val minX = left + inset
    val maxX = right - inset
    return if (maxX >= minX) {
        FloconMascotPlatform(minX = minX, maxX = maxX, y = top)
    } else {
        val middle = (left + right) / 2f
        FloconMascotPlatform(minX = middle, maxX = middle, y = top)
    }
}
