package io.github.openflocon.flocon.plugins.analytics.builder

import io.github.openflocon.flocon.plugins.analytics.FloconAnalyticsPlugin
import io.github.openflocon.flocon.plugins.analytics.model.AnalyticsEvent

class AnalyticsBuilder(
    val analyticsTableId: String,
    private val analyticsPlugin: FloconAnalyticsPlugin?,
) {
    fun logEvents(vararg events: AnalyticsEvent) {
        // no-op
    }

    fun logEvents(events: List<AnalyticsEvent>) {
        // no-op
    }
}
