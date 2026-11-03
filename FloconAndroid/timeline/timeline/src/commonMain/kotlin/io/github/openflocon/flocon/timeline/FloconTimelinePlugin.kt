package io.github.openflocon.flocon.timeline

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.encode
import io.github.openflocon.flocon.error.pluginNotInitialized
import io.github.openflocon.flocon.utils.currentTimeMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

class FloconTimelineConfig : FloconPluginConfig

enum class FloconTimelineLevel { Debug, Info, Warning, Error }

/**
 * The type of a timeline event. The reserved types are [Navigation], [UserAction] and [Lifecycle],
 * any other name is a custom type.
 */
@JvmInline
value class FloconTimelineType(val name: String) {
    companion object {
        val Navigation = FloconTimelineType("navigation")
        val UserAction = FloconTimelineType("userAction")
        val Lifecycle = FloconTimelineType("lifecycle")
    }
}

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

@Serializable
internal class TimelineEventSerializable(
    val type: String,
    val value: String,
    val params: Map<String, String> = emptyMap(),
    val level: String? = null,
    val timestamp: Long,
)

internal class FloconTimelinePluginImpl(
    private val sender: FloconMessageSender,
    private val encoder: FloconEncoder,
    dispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
) : FloconPlugin, FloconTimelinePlugin {

    // the desktop is read only for now: there is no message to receive
    override val key: String = Protocol.FromDevice.Timeline.Plugin

    // one coroutine at a time, so the events are sent in the order they were logged
    private val scope = CoroutineScope(dispatcher + SupervisorJob())

    override suspend fun onMessageReceived(method: String, body: String) {
        // no op
    }

    override suspend fun onConnectedToServer() {
        // no op
    }

    override fun log(type: FloconTimelineType, value: String, params: Map<String, String>) {
        send(type = type.name, value = value, params = params, level = null)
    }

    override fun log(
        tag: String,
        value: String,
        params: Map<String, String>,
        level: FloconTimelineLevel,
    ) {
        send(type = tag, value = value, params = params, level = level.toWire())
    }

    override fun logNavigation(value: String, params: Map<String, String>) =
        log(FloconTimelineType.Navigation, value, params)

    override fun logUserAction(value: String, params: Map<String, String>) =
        log(FloconTimelineType.UserAction, value, params)

    override fun logLifecycle(value: String, params: Map<String, String>) =
        log(FloconTimelineType.Lifecycle, value, params)

    private fun send(type: String, value: String, params: Map<String, String>, level: String?) {
        // the timestamp is the moment of the call, not the moment the message is sent
        val timestamp = currentTimeMillis()
        scope.launch {
            try {
                sender.send(
                    plugin = Protocol.FromDevice.Timeline.Plugin,
                    method = Protocol.FromDevice.Timeline.Method.Log,
                    body = encoder.encode(
                        TimelineEventSerializable(
                            type = type,
                            value = value,
                            params = params,
                            level = level,
                            timestamp = timestamp,
                        )
                    ),
                )
            } catch (t: Throwable) {
                FloconLogger.logError("timeline error", t)
            }
        }
    }

    private fun FloconTimelineLevel.toWire(): String = when (this) {
        FloconTimelineLevel.Debug -> "debug"
        FloconTimelineLevel.Info -> "info"
        FloconTimelineLevel.Warning -> "warning"
        FloconTimelineLevel.Error -> "error"
    }

    companion object {
        var plugin: FloconTimelinePlugin? = null
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
