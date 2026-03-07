package io.github.openflocon.flocon.plugins.analytics

import io.github.openflocon.flocon.*
import io.github.openflocon.flocon.plugins.analytics.builder.AnalyticsBuilder
import io.github.openflocon.flocon.plugins.analytics.model.AnalyticsItem

class FloconAnalyticsConfig

/**
 * Flocon Analytics Plugin.
 */
expect object FloconAnalytics : FloconPluginFactory<FloconAnalyticsConfig, FloconAnalyticsPlugin> {
    override fun createConfig(): FloconAnalyticsConfig
    override fun install(
        config: FloconAnalyticsConfig,
        app: FloconApp
    ): FloconAnalyticsPlugin

    override val name: String
}

fun floconAnalytics(analyticsName: String) : AnalyticsBuilder {
    return AnalyticsBuilder(
        analyticsTableId = analyticsName,
        analyticsPlugin = FloconApp.instance?.client?.analyticsPlugin,
    )
}

fun FloconApp.analytics(analyticsName: String): AnalyticsBuilder {
    return AnalyticsBuilder(
        analyticsTableId = analyticsName,
        analyticsPlugin = this.client?.analyticsPlugin,
    )
}

interface FloconAnalyticsPlugin : FloconPlugin {
    fun registerAnalytics(analyticsItems: List<AnalyticsItem>)
}