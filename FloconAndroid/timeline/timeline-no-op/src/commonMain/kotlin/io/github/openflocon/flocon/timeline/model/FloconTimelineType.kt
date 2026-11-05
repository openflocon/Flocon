package io.github.openflocon.flocon.timeline.model

import kotlin.jvm.JvmInline

@JvmInline
value class FloconTimelineType(val name: String) {
    companion object {
        val Navigation = FloconTimelineType("navigation")
        val UserAction = FloconTimelineType("userAction")
        val Lifecycle = FloconTimelineType("lifecycle")
    }
}
