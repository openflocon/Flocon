package io.github.openflocon.library.designsystem.components

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

enum class FloconMascotMode { Sit, Walk, Sleep, Happy }

/**
 * Distances and speed are in the same unit as the x passed to [FloconMascotState.update] (px).
 *
 * @param stopDistance how close the mascot gets to the pointer before sitting down
 * @param wakeDistance how far the pointer must be for a seated or sleeping mascot to get up
 */
class FloconMascotConfig(
    val stopDistance: Float,
    val wakeDistance: Float,
    val speedPerSecond: Float,
    val minSitMs: Long = 3_000,
    val sleepAfterMs: Long = 8_000,
    val stepMs: Long = 250,
)

/**
 * Behavior of the mascot: it stays still while the pointer is near, wakes up when the pointer gets
 * too far, walks next to it, sits for at least [FloconMascotConfig.minSitMs], then falls asleep.
 * It only moves horizontally, the caller owns the vertical position.
 */
@Stable
class FloconMascotState(
    startX: Float,
    private val config: FloconMascotConfig,
) {
    var x by mutableFloatStateOf(startX)
        private set
    var mode by mutableStateOf(FloconMascotMode.Sit)
        private set
    var facingRight by mutableStateOf(true)
        private set

    /** Toggles every [FloconMascotConfig.stepMs] while walking: which pair of legs is lifted. */
    var alternateLegs by mutableStateOf(false)
        private set
    var tailUp by mutableStateOf(false)
        private set
    var blinking by mutableStateOf(false)
        private set

    /** Toggles while [FloconMascotMode.Happy]: the mascot hops. */
    var hopUp by mutableStateOf(false)
        private set

    /** 0 or 1 while [FloconMascotMode.Happy]: hearts floating up. */
    var heartFrame by mutableIntStateOf(0)
        private set

    /** Number of "z" shown above the head while sleeping (1..2). */
    var sleepZCount by mutableIntStateOf(1)
        private set

    private var chasing = false
    private var sitMs = config.minSitMs
    private var idleMs = 0L
    private var walkMs = 0L
    private var happyMs = 0L

    /** The mascot has been clicked: it gets happy for a moment and stays where it is. */
    fun pet() {
        happyMs = HAPPY_MS
    }

    fun update(
        deltaMs: Long,
        timeMs: Long,
        targetX: Float,
        minX: Float,
        maxX: Float,
    ) {
        val target = targetX.coerceIn(minX, maxX)
        x = x.coerceIn(minX, maxX)

        val dx = target - x
        val distance = abs(dx)

        val happy = happyMs > 0
        if (happy) {
            happyMs = (happyMs - deltaMs).coerceAtLeast(0)
            chasing = false
        }

        sitMs = if (chasing || happy) 0 else sitMs + deltaMs
        if (!chasing && !happy && distance > config.wakeDistance && sitMs >= config.minSitMs) chasing = true
        if (chasing && distance <= config.stopDistance) chasing = false

        var step = 0f
        if (chasing) {
            step = min(distance - config.stopDistance, config.speedPerSecond * deltaMs / 1_000f)
            x += dx.sign * step
        }
        val moving = step > 0f

        idleMs = if (moving || happy) 0 else idleMs + deltaMs
        mode = when {
            happy -> FloconMascotMode.Happy
            moving -> FloconMascotMode.Walk
            idleMs > config.sleepAfterMs -> FloconMascotMode.Sleep
            else -> FloconMascotMode.Sit
        }
        if (distance > FACING_THRESHOLD) facingRight = dx > 0

        if (moving) walkMs += deltaMs
        alternateLegs = (walkMs / config.stepMs) % 2 == 1L
        tailUp = (timeMs / TAIL_MS) % 2 == 1L
        blinking = timeMs % BLINK_PERIOD_MS < BLINK_MS
        hopUp = happy && (happyMs / HOP_MS) % 2 == 0L
        heartFrame = ((happyMs / HEART_MS) % 2).toInt()
        sleepZCount = ((timeMs / Z_MS) % 2).toInt() + 1
    }

    private companion object {
        const val FACING_THRESHOLD = 6f
        const val TAIL_MS = 300L
        const val BLINK_PERIOD_MS = 3_600L
        const val BLINK_MS = 160L
        const val Z_MS = 700L
        const val HAPPY_MS = 1_600L
        const val HOP_MS = 160L
        const val HEART_MS = 320L
    }
}
