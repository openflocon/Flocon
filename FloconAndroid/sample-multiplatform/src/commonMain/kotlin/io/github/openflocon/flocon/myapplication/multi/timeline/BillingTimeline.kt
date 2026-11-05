package io.github.openflocon.flocon.myapplication.multi.timeline

import io.github.openflocon.flocon.timeline.FloconTimelinePlugin
import io.github.openflocon.flocon.timeline.model.FloconTimelineType

/** A custom type, declared once... */
val Billing = FloconTimelineType("billing")

/** ...and wrapped in an extension, so the call sites read like the built-in ones. */
fun FloconTimelinePlugin.logBilling(value: String, params: Map<String, String> = emptyMap()) =
    log(Billing, value, params)
