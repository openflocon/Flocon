package io.github.openflocon.flocon.accessibility

/**
 * Tells a tap from a long press, a drag or a multi-touch gesture, from the raw touch stream.
 * Pure Kotlin so it can be unit tested without Android.
 *
 * A tap is a down followed by an up that stays within [touchSlop] of the down point and happens
 * within [maxDurationMs].
 */
internal class TapDetector(
    private val touchSlop: Float,
    private val maxDurationMs: Long,
) {
    private var active = false
    private var downX = 0f
    private var downY = 0f
    private var downTime = 0L

    fun onDown(x: Float, y: Float, time: Long) {
        active = true
        downX = x
        downY = y
        downTime = time
    }

    fun onMove(x: Float, y: Float) {
        if (active && isOutOfSlop(x, y)) active = false
    }

    /** @return true when the gesture that just ended is a tap */
    fun onUp(x: Float, y: Float, time: Long): Boolean {
        val isTap = active && !isOutOfSlop(x, y) && time - downTime <= maxDurationMs
        active = false
        return isTap
    }

    /** A cancelled gesture, or one that became multi-touch. */
    fun cancel() {
        active = false
    }

    private fun isOutOfSlop(x: Float, y: Float): Boolean {
        val dx = x - downX
        val dy = y - downY
        return dx * dx + dy * dy > touchSlop * touchSlop
    }
}
