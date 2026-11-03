package io.github.openflocon.flocon.timeline

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.core.FloconEncoder
import kotlin.jvm.JvmInline

class FloconTimelineConfig : FloconPluginConfig

enum class FloconTimelineLevel { Debug, Info, Warning, Error }

@JvmInline
value class FloconTimelineType(val name: String) {
    companion object {
        val Navigation = FloconTimelineType("navigation")
        val UserAction = FloconTimelineType("userAction")
        val Lifecycle = FloconTimelineType("lifecycle")
    }
}

interface FloconTimelinePlugin : FloconPlugin {
    fun log(type: FloconTimelineType, value: String, params: Map<String, String> = emptyMap())
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
    override val pluginId: String = "timeline"
    override fun createConfig(context: FloconContext) = FloconTimelineConfig()
    override fun install(
        pluginConfig: FloconTimelineConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder,
    ): FloconTimelinePlugin = FloconTimelinePluginNoOp
}

internal object FloconTimelinePluginNoOp : FloconTimelinePlugin {
    override val key: String = "timeline"
    override fun log(type: FloconTimelineType, value: String, params: Map<String, String>) = Unit
    override fun log(tag: String, value: String, params: Map<String, String>, level: FloconTimelineLevel) = Unit
    override fun logNavigation(value: String, params: Map<String, String>) = Unit
    override fun logUserAction(value: String, params: Map<String, String>) = Unit
    override fun logLifecycle(value: String, params: Map<String, String>) = Unit
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}

val Flocon.Companion.timelinePlugin: FloconTimelinePlugin
    get() = FloconTimelinePluginNoOp
