package io.github.openflocon.flocon.plugins.analytics

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.analytics.builder.AnalyticsBuilder
import io.github.openflocon.flocon.plugins.analytics.model.AnalyticsItem

fun FloconApp.analytics(analyticsName: String): AnalyticsBuilder {
    return AnalyticsBuilder(
        analyticsTableId = analyticsName,
        analyticsPlugin = this.client?.analyticsPlugin,
    )
}

interface FloconAnalyticsPlugin {
    fun registerAnalytics(analyticsItems: List<AnalyticsItem>)
}