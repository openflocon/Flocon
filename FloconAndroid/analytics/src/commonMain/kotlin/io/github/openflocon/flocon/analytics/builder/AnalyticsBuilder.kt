@file:OptIn(ExperimentalUuidApi::class)

package io.github.openflocon.flocon.analytics.builder

import io.github.openflocon.flocon.analytics.FloconAnalyticsPlugin
import io.github.openflocon.flocon.analytics.model.AnalyticsEvent
import io.github.openflocon.flocon.analytics.model.AnalyticsItem
import io.github.openflocon.flocon.utils.currentTimeMillis
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
                id = Uuid.random().toString(),
                analyticsTableId = analyticsTableId,
                eventName = it.eventName,
                createdAt = currentTimeMillis(),
                properties = it.properties,
            )
        }
        analyticsPlugin?.registerAnalytics(analyticsItems)
    }
}
