package io.github.openflocon.flocon.plugins.analytics

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.analytics.builder.AnalyticsBuilder
import io.github.openflocon.flocon.plugins.analytics.model.AnalyticsItem
import org.json.JSONArray
import java.util.concurrent.ConcurrentLinkedQueue

fun Flocon.analytics(analyticsName: String): AnalyticsBuilder {
    return AnalyticsBuilder(
        analyticsTableId = analyticsName,
        analyticsPlugin = this.client?.analyticsPlugin,
    )
}

interface FloconAnalyticsPlugin : FloconPlugin {
    fun registerAnalytics(analyticsItems: List<AnalyticsItem>)
}

class FloconAnalyticsPluginImpl(
    private val sender: FloconMessageSender,
) : FloconAnalyticsPlugin {

    private val analyticsMessages = ConcurrentLinkedQueue<AnalyticsItem>()

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
        sender: FloconMessageSender,
    ) {
        when(messageFromServer.method) {
            Protocol.ToDevice.Analytics.Method.ClearItems-> {
                val items = readIds(messageFromServer.body)
                if(items.isNotEmpty()) {
                    clearItems(items.toSet())
                }
            }
        }
    }

    private fun readIds(body: String) : List<String> {
        return try {
            val array = JSONArray(body)
            val items = mutableListOf<String>()
            for (i in 0 until array.length()) {
                items.add(array.getString(i))
            }
            items
        } catch (t: Throwable) {
            t.printStackTrace()
            emptyList()
        }
    }

    override fun onConnectedToServer(sender: FloconMessageSender) {
        sendAnalytics()
    }

    override fun registerAnalytics(analyticsItems: List<AnalyticsItem>) {
        analyticsMessages.addAll(analyticsItems)
        sendAnalytics()
    }

    private fun sendAnalytics() {
        analyticsMessages.takeIf { it.isNotEmpty() }?.let { toSend ->
            sender.send(
                plugin = Protocol.FromDevice.Analytics.Plugin,
                method = Protocol.FromDevice.Analytics.Method.AddItems,
                body = AnalyticsItem.listToJson(toSend).toString()
            )
        }
    }

    private fun clearItems(ids: Set<String>) {
        val iterator = analyticsMessages.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.id in ids) {
                iterator.remove()
            }
        }
    }
}