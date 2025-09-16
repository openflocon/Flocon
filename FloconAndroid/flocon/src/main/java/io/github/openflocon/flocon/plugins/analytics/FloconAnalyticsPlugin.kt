package io.github.openflocon.flocon.plugins.analytics

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.analytics.model.AnalyticsItem
import io.github.openflocon.flocon.plugins.analytics.mapper.analyticsItemsToJson

internal class FloconAnalyticsPluginImpl(
    private val sender: FloconMessageSender,
) : FloconAnalyticsPlugin {

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
        sender: FloconMessageSender,
    ) {
        // no op
    }

    override fun onConnectedToServer(sender: FloconMessageSender) {
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
                    body = analyticsItemsToJson(toSend).toString()
                )
            } catch (t: Throwable) {
                FloconLogger.logError("error on sendAnalytics", t)
            }
        }
    }
}