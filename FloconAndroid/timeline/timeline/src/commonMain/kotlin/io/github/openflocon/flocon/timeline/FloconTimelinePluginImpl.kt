package io.github.openflocon.flocon.timeline

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.encode
import io.github.openflocon.flocon.timeline.mapper.TimelineEventSerializable
import io.github.openflocon.flocon.timeline.mapper.toRemote
import io.github.openflocon.flocon.timeline.model.FloconTimelineLevel
import io.github.openflocon.flocon.timeline.model.FloconTimelineType
import io.github.openflocon.flocon.utils.currentTimeMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

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
        send(type = tag, value = value, params = params, level = level.toRemote())
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

    companion object {
        var plugin: FloconTimelinePlugin? = null
    }
}
