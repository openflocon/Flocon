package io.github.openflocon.flocon.pluginsold.analytics

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.pluginsold.analytics.model.AnalyticsItem

class FloconAnalyticsConfig : FloconPluginConfig

/**
 * Flocon Analytics Plugin.
 */
object FloconAnalytics : FloconPluginFactory<FloconAnalyticsConfig, FloconAnalyticsPlugin> {
    override fun createConfig(): FloconAnalyticsConfig = TODO()
    override fun install(
        pluginConfig: FloconAnalyticsConfig,
        floconConfig: FloconConfig
    ): FloconAnalyticsPlugin = TODO()

    override val name: String = ""
    override val pluginId: String = "ANALYTICS"
}
//
//fun floconAnalytics(analyticsName: String) : AnalyticsBuilder {
//    return AnalyticsBuilder(
//        analyticsTableId = analyticsName,
//        analyticsPlugin = FloconApp.instance?.client?.analyticsPlugin,
//    )
//}

//fun FloconApp.analytics(analyticsName: String): AnalyticsBuilder {
//    return AnalyticsBuilder(
//        analyticsTableId = analyticsName,
//        analyticsPlugin = this.client?.analyticsPlugin,
//    )
//}

interface FloconAnalyticsPlugin : FloconPlugin {
    fun registerAnalytics(analyticsItems: List<AnalyticsItem>)
}