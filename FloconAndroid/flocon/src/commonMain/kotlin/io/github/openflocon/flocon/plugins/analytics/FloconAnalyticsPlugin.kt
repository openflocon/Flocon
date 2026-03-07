package io.github.openflocon.flocon.plugins.analytics

import io.github.openflocon.flocon.*
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.plugins.analytics.model.AnalyticsItem
import io.github.openflocon.flocon.plugins.analytics.mapper.analyticsItemsToJson

actual object FloconAnalytics : FloconPluginFactory<FloconAnalyticsConfig, FloconAnalyticsPlugin> {
    override val name: String = "Analytics"
    override val pluginId: String = Protocol.ToDevice.Analytics.Plugin
    override fun createConfig() = FloconAnalyticsConfig()
    override fun install(config: FloconAnalyticsConfig, app: FloconApp): FloconAnalyticsPlugin {
        return FloconAnalyticsPluginImpl(
            sender = app.client as FloconMessageSender
        )
    }
}

internal class FloconAnalyticsPluginImpl(
    private val sender: FloconMessageSender,
) : FloconPlugin, FloconAnalyticsPlugin {

    override fun onMessageReceived(
        method: String,
        body: String,
    ) {
        // no op
    }

    override fun onConnectedToServer() {
        // no op
    }

    override fun registerAnalytics(analyticsItems: List<AnalyticsItem>) {
        sendAnalytics(analyticsItems)
    }

    private fun sendAnalytics(analyticsItems: List<AnalyticsItem>) {
        analyticsItems.takeIf { it.isNotEmpty() }?.forEach { toSend ->
            try {
                sender.send(
                    plugin = Protocol.FromDevice.Analytics.Plugin,
                    method = Protocol.FromDevice.Analytics.Method.AddItems,
                    body = analyticsItemsToJson(toSend)
                )
            } catch (t: Throwable) {
                FloconLogger.logError("error on sendAnalytics", t)
            }
        }
    }
}