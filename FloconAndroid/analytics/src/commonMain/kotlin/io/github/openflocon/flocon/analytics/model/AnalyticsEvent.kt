package io.github.openflocon.flocon.analytics.model

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.analytics.analyticsPlugin

data class AnalyticsEvent(
    val eventName: String,
    val analyticsTableId: String,
    val properties: List<AnalyticsPropertiesConfig>,
) {
    constructor(
        eventName: String,
        analyticsTableId: String,
        vararg properties: AnalyticsPropertiesConfig,
    ) : this(
        eventName = eventName,
        analyticsTableId = analyticsTableId,
        properties = properties.toList()
    )
}

fun analyticsEvent(
    name: String,
    analyticsTableId: String,
    properties: List<AnalyticsPropertiesConfig>
) {
    Flocon.analyticsPlugin
        .log(
            listOf(
                AnalyticsEvent(
                    eventName = name,
                    analyticsTableId = analyticsTableId,
                    properties = properties
                )
            )
        )
}