package io.github.openflocon.library.designsystem.components

/**
 * A short scene the mascot may play while it sits and waits, before it falls asleep.
 * Each one is a list of pictures (see `drawMascotIdle`), each shown for some time.
 */
enum class FloconMascotIdleAnimation(
    private val steps: List<Step>,
) {
    /** Sitting, scratches its ear with the whole hind leg. */
    Scratch(
        listOf(Step(0, 700), Step(1, 350)) +
            List(4) { listOf(Step(2, 180), Step(3, 180)) }.flatten() +
            Step(4, 800),
    ),

    /** Stretches forwards and down, opens its mouth once on the ground, then gets back up. */
    Stretch(
        listOf(
            Step(0, 600),
            Step(1, 450),
            Step(2, 450),
            Step(3, 350),
            Step(4, 1_100),
            Step(3, 300),
            Step(2, 450),
            Step(1, 450),
            Step(5, 800),
        ),
    ),

    /** Gets up, puts its nose on the ground and sniffs going back and forth. */
    Sniff(
        listOf(Step(0, 450), Step(1, 450)) +
            List(3) { listOf(Step(2, 240), Step(3, 240)) }.flatten() +
            Step(4, 800),
    ),

    /** Finds a ball, takes it, throws it in the air, then puts it down between its paws. */
    Ball(
        listOf(Step(0, 800), Step(1, 700), Step(2, 700), Step(3, 600), Step(4, 1_000)),
    ),
    ;

    /** Picture number [frame], shown for [ms]. */
    class Step(val frame: Int, val ms: Long)

    val durationMs: Long = steps.sumOf { it.ms }

    /** The picture to show [elapsedMs] after the start. */
    fun frameAt(elapsedMs: Long): Int {
        var left = elapsedMs
        for (step in steps) {
            if (left < step.ms) return step.frame
            left -= step.ms
        }
        return steps.last().frame
    }
}
