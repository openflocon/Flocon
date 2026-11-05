package io.github.openflocon.flocon.timeline.model

import kotlin.jvm.JvmInline

/**
 * The type of a timeline event. The reserved types are [Navigation], [UserAction] and [Lifecycle],
 * any other name is a custom type.
 */
@JvmInline
value class FloconTimelineType(val name: String) {
    companion object {
        val Navigation = FloconTimelineType("navigation")
        val UserAction = FloconTimelineType("userAction")
        val Lifecycle = FloconTimelineType("lifecycle")
    }
}
