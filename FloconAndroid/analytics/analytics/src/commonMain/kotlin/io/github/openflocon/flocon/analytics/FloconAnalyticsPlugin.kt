package io.github.openflocon.flocon.analytics

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.analytics.model.AnalyticsEvent
import io.github.openflocon.flocon.analytics.model.AnalyticsPropertiesConfig
import io.github.openflocon.flocon.analytics.model.AnalyticsItem
import io.github.openflocon.flocon.analytics.model.toItem
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.encode
import io.github.openflocon.flocon.error.pluginNotInitialized

class FloconAnalyticsConfig : FloconPluginConfig

interface FloconAnalyticsPlugin : FloconPlugin {
    fun log(analyticsItems: List<AnalyticsEvent>)
    fun log(analyticsItem: AnalyticsEvent)

    /** Returns a sender bound to [analyticsTableId], e.g. `Flocon.analyticsPlugin.sender("firebase")`. */
    fun sender(analyticsTableId: String): FloconAnalyticsSender
}

/** Logs events to a fixed analytics table, so the table id is not repeated at each call. */
class FloconAnalyticsSender internal constructor(
    private val plugin: FloconAnalyticsPlugin,
    val analyticsTableId: String,
) {
    fun log(eventName: String, properties: List<AnalyticsPropertiesConfig> = emptyList()) =
        plugin.log(AnalyticsEvent(eventName, analyticsTableId, properties))

    fun log(eventName: String, vararg properties: AnalyticsPropertiesConfig) =
        log(eventName, properties.toList())

    fun log(event: AnalyticsEvent) = plugin.log(event.copy(analyticsTableId = analyticsTableId))
}

object FloconAnalytics : FloconPluginFactory<FloconAnalyticsConfig, FloconAnalyticsPlugin> {
    override val name: String = "Analytics"
    override val pluginId: String = Protocol.ToDevice.Analytics.Plugin
    override fun createConfig(context: FloconContext) = FloconAnalyticsConfig()
    override fun install(
        pluginConfig: FloconAnalyticsConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconAnalyticsPlugin {
        return FloconAnalyticsPluginImpl(
            sender = floconConfig.client as FloconMessageSender,
            encoder = encoder
        )
            .also { FloconAnalyticsPluginImpl.plugin = it }
    }
}

internal class FloconAnalyticsPluginImpl(
    private val sender: FloconMessageSender,
    private val encoder: FloconEncoder
) : FloconPlugin, FloconAnalyticsPlugin {
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

    override fun log(analyticsItem: AnalyticsEvent) {
        log(listOf(analyticsItem))
    }

    override fun log(analyticsItems: List<AnalyticsEvent>) {
        sendAnalytics(analyticsItems)
    }

    override fun sender(analyticsTableId: String) = FloconAnalyticsSender(this, analyticsTableId)

    private fun sendAnalytics(analyticsItems: List<AnalyticsEvent>) {
        analyticsItems.takeIf { it.isNotEmpty() }
            ?.map(AnalyticsEvent::toItem)
            ?.let { toSend ->
                try {
                    sender.send(
                        plugin = Protocol.FromDevice.Analytics.Plugin,
                        method = Protocol.FromDevice.Analytics.Method.AddItems,
                        body = encoder.encode<List<AnalyticsItem>>(toSend)
                    )
                } catch (t: Throwable) {
                    FloconLogger.logError("error on sendAnalytics", t)
                }
            }
    }

    companion object {
        var plugin: FloconAnalyticsPlugin? = null
    }
}

val Flocon.Companion.analyticsPlugin: FloconAnalyticsPlugin
    get() = FloconAnalyticsPluginImpl.plugin ?: pluginNotInitialized("Analytics", NoOpAnalyticsPlugin())

private class NoOpAnalyticsPlugin : FloconAnalyticsPlugin {
    override val key: String = ""
    override fun log(analyticsItems: List<AnalyticsEvent>) = Unit
    override fun log(analyticsItem: AnalyticsEvent) = Unit
    override fun sender(analyticsTableId: String) = FloconAnalyticsSender(this, analyticsTableId)
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}