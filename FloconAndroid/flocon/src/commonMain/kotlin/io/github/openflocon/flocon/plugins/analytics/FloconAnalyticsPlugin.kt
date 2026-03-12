package io.github.openflocon.flocon.plugins.analytics

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.plugins.analytics.model.AnalyticsItem

class FloconAnalyticsConfig : FloconPluginConfig

interface FloconAnalyticsPlugin : FloconPlugin {
    fun registerAnalytics(analyticsItems: List<AnalyticsItem>)
}

object FloconAnalytics : FloconPluginFactory<FloconAnalyticsConfig, FloconAnalyticsPlugin> {
    override val name: String = "Analytics"
    override val pluginId: String = Protocol.ToDevice.Analytics.Plugin
    override fun createConfig() = FloconAnalyticsConfig()
    override fun install(
        pluginConfig: FloconAnalyticsConfig,
        floconConfig: FloconConfig
    ): FloconAnalyticsPlugin {
        return FloconAnalyticsPluginImpl(
            sender = floconConfig.client as FloconMessageSender
        )
    }
}

internal class FloconAnalyticsPluginImpl(
    private val sender: FloconMessageSender,
) : FloconPlugin, FloconAnalyticsPlugin {
    override val key: String
        get() = "ANALYTICS"

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
        sendAnalytics(analyticsItems)
    }

    private fun sendAnalytics(analyticsItems: List<AnalyticsItem>) {
        analyticsItems.takeIf { it.isNotEmpty() }?.forEach { toSend ->
            try {
//                sender.send(
//                    plugin = Protocol.FromDevice.Analytics.Plugin,
//                    method = Protocol.FromDevice.Analytics.Method.AddItems,
//                    body = analyticsItemsToJson(toSend)
//                )
            } catch (t: Throwable) {
                FloconLogger.logError("error on sendAnalytics", t)
            }
        }
    }
}