package io.github.openflocon.flocon.plugins.analytics

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.analytics.model.AnalyticsItem
import io.github.openflocon.flocon.plugins.analytics.mapper.analyticsItemsToJson
import kotlinx.serialization.json.Json

internal class FloconAnalyticsPluginImpl(
    private val sender: FloconMessageSender,
    private val json: Json,
) : FloconPlugin, FloconAnalyticsPlugin {

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
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
                    body = analyticsItemsToJson(item = toSend, json = json)
                )
            } catch (t: Throwable) {
                FloconLogger.logError("error on sendAnalytics", t)
            }
        }
    }
}