package io.github.openflocon.library.designsystem.components

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

enum class FloconMascotMode { Sit, Walk, Sleep, Lie, Happy, Petted }

/**
 * Distances and speed are in the same unit as the x passed to [FloconMascotState.update] (px).
 *
 * @param stopDistance how close the mascot gets to the pointer before sitting down
 * @param wakeDistance how far the pointer must be for a seated or sleeping mascot to get up
 * @param runDistance how far the pointer must be for the mascot to run instead of walk
 */
class FloconMascotConfig(
    val stopDistance: Float,
    val wakeDistance: Float,
    val speedPerSecond: Float,
    val runDistance: Float,
    val runSpeedPerSecond: Float,
    val minSitMs: Long = 3_000,
    val sleepAfterMs: Long = 4_000,
    val lieAfterMs: Long = 8_000,
    val minLieMs: Long = 6_000,
    val stepMs: Long = 250,
    val runStepMs: Long = 130,
    val minTurnIntervalMs: Long = 600,
)

/**
 * Behavior of the mascot: it stays still while the pointer is near, wakes up when the pointer gets
 * too far, walks next to it, sits for at least [FloconMascotConfig.minSitMs], then falls asleep and,
 * after a long time, lies down with its head on the ground for at least [FloconMascotConfig.minLieMs].
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

    /** True while it hurries after a far pointer: faster legs and speed. */
    var running by mutableStateOf(false)
        private set
    var blinking by mutableStateOf(false)
        private set

    /** Toggles while [FloconMascotMode.Happy]: the mascot hops. */
    var hopUp by mutableStateOf(false)
        private set

    /** Time spent being petted without a break, drives the hearts floating up. */
    var petClockMs by mutableLongStateOf(0L)
        private set

    /** 0 or 1 while happy or petted: hearts floating up. */
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
    private var lieMs = 0L
    private var pettedMs = 0L
    private var hoverX = 0f
    private var hoverTravel = 0f
    private var strokes = 0
    private var lastStrokeMs = Long.MIN_VALUE
    private var sinceTurnMs = config.minTurnIntervalMs

    /**
     * The pointer moved over the mascot, at [x] (px, in the mascot's own coordinates). Moving back
     * and forth over it, each swing being at least [minStroke] long, is a caress.
     */
    fun onHover(x: Float, timeMs: Long, minStroke: Float) {
        val dx = x - hoverX
        hoverX = x
        if (dx == 0f) return
        if (hoverTravel != 0f && dx.sign != hoverTravel.sign) {
            if (abs(hoverTravel) >= minStroke) {
                strokes = if (timeMs - lastStrokeMs > STROKE_WINDOW_MS) 1 else strokes + 1
                lastStrokeMs = timeMs
                if (strokes >= STROKES_TO_PET) pettedMs = PETTED_HOLD_MS
            }
            hoverTravel = dx
        } else {
            hoverTravel += dx
        }
    }

    /** The pointer left the mascot. */
    fun onHoverEnd() {
        hoverTravel = 0f
        strokes = 0
    }

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
        val petted = pettedMs > 0 && !happy
        if (happy) happyMs = (happyMs - deltaMs).coerceAtLeast(0)
        if (pettedMs > 0) pettedMs = (pettedMs - deltaMs).coerceAtLeast(0)
        val still = happy || petted
        if (still) chasing = false

        sitMs = if (chasing || still) 0 else sitMs + deltaMs
        val rested = mode != FloconMascotMode.Lie || lieMs >= config.minLieMs
        if (!chasing && !still && rested && distance > config.wakeDistance && sitMs >= config.minSitMs) chasing = true
        if (chasing && distance <= config.stopDistance) chasing = false

        // it runs while the pointer is far, and keeps running until it has gotten much closer
        running = chasing && distance > if (running) config.runDistance * RUN_RELEASE else config.runDistance

        var step = 0f
        if (chasing) {
            val speed = if (running) config.runSpeedPerSecond else config.speedPerSecond
            step = min(distance - config.stopDistance, speed * deltaMs / 1_000f)
            x += dx.sign * step
        }
        val moving = step > 0f

        idleMs = if (moving || still) 0 else idleMs + deltaMs
        mode = when {
            happy -> FloconMascotMode.Happy
            petted -> FloconMascotMode.Petted
            moving -> FloconMascotMode.Walk
            idleMs > config.lieAfterMs -> FloconMascotMode.Lie
            idleMs > config.sleepAfterMs -> FloconMascotMode.Sleep
            else -> FloconMascotMode.Sit
        }
        // it can't turn around more often than every minTurnIntervalMs, so a pointer moving quickly
        // from side to side doesn't make it flip back and forth
        sinceTurnMs += deltaMs
        if (distance > FACING_THRESHOLD && (dx > 0) != facingRight && sinceTurnMs >= config.minTurnIntervalMs) {
            facingRight = dx > 0
            sinceTurnMs = 0
        }

        lieMs = if (mode == FloconMascotMode.Lie) lieMs + deltaMs else 0
        if (moving) walkMs += deltaMs
        alternateLegs = (walkMs / (if (running) config.runStepMs else config.stepMs)) % 2 == 1L
        tailUp = (timeMs / TAIL_MS) % 2 == 1L
        blinking = timeMs % BLINK_PERIOD_MS < BLINK_MS
        hopUp = happy && (happyMs / HOP_MS) % 2 == 0L
        petClockMs = if (petted) petClockMs + deltaMs else 0L
        heartFrame = ((maxOf(happyMs, pettedMs) / HEART_MS) % 2).toInt()
        sleepZCount = ((timeMs / Z_MS) % 2).toInt() + 1
    }

    private companion object {
        const val FACING_THRESHOLD = 6f
        const val TAIL_MS = 300L
        const val BLINK_PERIOD_MS = 3_600L
        const val BLINK_MS = 160L
        const val Z_MS = 700L
        const val RUN_RELEASE = 0.5f
        const val STROKES_TO_PET = 4
        const val STROKE_WINDOW_MS = 800L
        const val PETTED_HOLD_MS = 900L
        const val HAPPY_MS = 1_600L
        const val HOP_MS = 160L
        const val HEART_MS = 320L
    }
}
