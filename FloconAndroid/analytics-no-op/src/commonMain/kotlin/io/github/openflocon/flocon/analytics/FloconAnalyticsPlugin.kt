package io.github.openflocon.flocon.analytics

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.analytics.model.AnalyticsEvent
import io.github.openflocon.flocon.core.FloconEncoder

class FloconAnalyticsConfig : FloconPluginConfig

interface FloconAnalyticsPlugin : FloconPlugin {
    fun log(analyticsItems: List<AnalyticsEvent>)
    fun log(analyticsItem: AnalyticsEvent)
}

object FloconAnalytics : FloconPluginFactory<FloconAnalyticsConfig, FloconAnalyticsPlugin> {
    override val name: String = "Analytics"
    override val pluginId: String = "Analytics"
    override fun createConfig(context: FloconContext) = FloconAnalyticsConfig()
    override fun install(
        pluginConfig: FloconAnalyticsConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconAnalyticsPlugin = FloconAnalyticsPluginNoOp
}

internal object FloconAnalyticsPluginNoOp : FloconAnalyticsPlugin {
    override val key: String = "Analytics"
    override fun log(analyticsItems: List<AnalyticsEvent>) = Unit
    override fun log(analyticsItem: AnalyticsEvent) = Unit
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}

val Flocon.Companion.analyticsPlugin: FloconAnalyticsPlugin
    get() = FloconAnalyticsPluginNoOp
