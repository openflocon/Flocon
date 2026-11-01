package io.github.openflocon.flocon.analytics

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.analytics.model.AnalyticsEvent
import io.github.openflocon.flocon.analytics.model.AnalyticsPropertiesConfig
import io.github.openflocon.flocon.core.FloconEncoder

class FloconAnalyticsConfig : FloconPluginConfig

interface FloconAnalyticsPlugin : FloconPlugin {
    fun log(analyticsItems: List<AnalyticsEvent>)
    fun log(analyticsItem: AnalyticsEvent)

    /** Returns a sender bound to [analyticsTableId], e.g. `Flocon.analyticsPlugin.sender("firebase")`. */
    fun sender(analyticsTableId: String): FloconAnalyticsSender
}

class FloconAnalyticsSender internal constructor(
    val analyticsTableId: String,
) {
    fun log(eventName: String, properties: List<AnalyticsPropertiesConfig> = emptyList()) = Unit
    fun log(eventName: String, vararg properties: AnalyticsPropertiesConfig) = Unit
    fun log(event: AnalyticsEvent) = Unit
}

object FloconAnalytics : FloconPluginFactory<FloconAnalyticsConfig, FloconAnalyticsPlugin> {
    override val name: String = "Analytics"
    override val pluginId: String = "analytics"
    override fun createConfig(context: FloconContext) = FloconAnalyticsConfig()
    override fun install(
        pluginConfig: FloconAnalyticsConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconAnalyticsPlugin = FloconAnalyticsPluginNoOp
}

internal object FloconAnalyticsPluginNoOp : FloconAnalyticsPlugin {
    override val key: String = "analytics"
    override fun log(analyticsItems: List<AnalyticsEvent>) = Unit
    override fun log(analyticsItem: AnalyticsEvent) = Unit
    override fun sender(analyticsTableId: String) = FloconAnalyticsSender(analyticsTableId)
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}

val Flocon.Companion.analyticsPlugin: FloconAnalyticsPlugin
    get() = FloconAnalyticsPluginNoOp
