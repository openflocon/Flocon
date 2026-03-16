package io.github.openflocon.flocon.plugins.analytics

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.plugins.analytics.model.AnalyticsItem

class FloconAnalyticsConfig : FloconPluginConfig

interface FloconAnalyticsPlugin : FloconPlugin {
    fun registerAnalytics(analyticsItems: List<AnalyticsItem>)
}

object FloconAnalytics : FloconPluginFactory<FloconAnalyticsConfig, FloconAnalyticsPlugin> {
    override val name: String = "Analytics"
    override val pluginId: String = Protocol.ToDevice.Analytics.Plugin
    override fun createConfig(context: FloconContext) = FloconAnalyticsConfig()
    override fun install(
        pluginConfig: FloconAnalyticsConfig,
        floconConfig: FloconConfig
    ): FloconAnalyticsPlugin {
        return FloconAnalyticsNoOpImpl()
    }
}

internal class FloconAnalyticsNoOpImpl : FloconPlugin, FloconAnalyticsPlugin {
    override val key: String
        get() = Protocol.ToDevice.Analytics.Plugin

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        // no op
    }

    override suspend fun onConnectedToServer() {
        // no op
    }

    override fun registerAnalytics(analyticsItems: List<AnalyticsItem>) {
        // no op
    }
}
