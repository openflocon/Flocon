package io.github.openflocon.flocon.timeline

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.error.pluginNotInitialized
import io.github.openflocon.flocon.timeline.model.FloconTimelineLevel
import io.github.openflocon.flocon.timeline.model.FloconTimelineType

class FloconTimelineConfig : FloconPluginConfig

interface FloconTimelinePlugin : FloconPlugin {
    /** Logs an event of the given [type], without any level. */
    fun log(type: FloconTimelineType, value: String, params: Map<String, String> = emptyMap())

    /** Logs a custom event: the [tag] is the type of the event, with a [level]. */
    fun log(
        tag: String,
        value: String,
        params: Map<String, String> = emptyMap(),
        level: FloconTimelineLevel = FloconTimelineLevel.Info,
    )

    fun logNavigation(value: String, params: Map<String, String> = emptyMap())
    fun logUserAction(value: String, params: Map<String, String> = emptyMap())
    fun logLifecycle(value: String, params: Map<String, String> = emptyMap())
}

object FloconTimeline : FloconPluginFactory<FloconTimelineConfig, FloconTimelinePlugin> {
    override val name: String = "Timeline"
    override val pluginId: String = Protocol.FromDevice.Timeline.Plugin
    override fun createConfig(context: FloconContext) = FloconTimelineConfig()
    override fun install(
        pluginConfig: FloconTimelineConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder,
    ): FloconTimelinePlugin {
        return FloconTimelinePluginImpl(
            sender = floconConfig.client as FloconMessageSender,
            encoder = encoder,
        )
            .also { FloconTimelinePluginImpl.plugin = it }
    }
}

val Flocon.Companion.timelinePlugin: FloconTimelinePlugin
    get() = FloconTimelinePluginImpl.plugin ?: pluginNotInitialized("Timeline", NoOpTimelinePlugin())

private class NoOpTimelinePlugin : FloconTimelinePlugin {
    override val key: String = ""
    override fun log(type: FloconTimelineType, value: String, params: Map<String, String>) = Unit
    override fun log(tag: String, value: String, params: Map<String, String>, level: FloconTimelineLevel) = Unit
    override fun logNavigation(value: String, params: Map<String, String>) = Unit
    override fun logUserAction(value: String, params: Map<String, String>) = Unit
    override fun logLifecycle(value: String, params: Map<String, String>) = Unit
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}
