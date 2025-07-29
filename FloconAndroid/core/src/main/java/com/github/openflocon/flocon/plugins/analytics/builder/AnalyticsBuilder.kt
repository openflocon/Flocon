package com.github.openflocon.flocon.plugins.analytics.builder

import com.github.openflocon.flocon.plugins.analytics.FloconAnalyticsPlugin
import com.github.openflocon.flocon.plugins.analytics.model.AnalyticsEvent
import com.github.openflocon.flocon.plugins.analytics.model.AnalyticsItem
import java.util.UUID

class AnalyticsBuilder(
    val analyticsTableId: String,
    private val analyticsPlugin: FloconAnalyticsPlugin?,
) {
    fun logEvents(vararg events: AnalyticsEvent) {
        this.logEvents(events.toList())
    }

    fun logEvents(events: List<AnalyticsEvent>) {
        val analyticsItems = events.map {
            AnalyticsItem(
                id = UUID.randomUUID().toString(),
                analyticsTableId = analyticsTableId,
                eventName = it.eventName,
                createdAt = System.currentTimeMillis(),
                properties = it.properties,
            )
        }
        analyticsPlugin?.registerAnalytics(analyticsItems)
    }
}