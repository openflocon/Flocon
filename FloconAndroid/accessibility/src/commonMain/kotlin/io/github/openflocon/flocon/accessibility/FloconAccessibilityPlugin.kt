package io.github.openflocon.flocon.accessibility

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.accessibility.model.AccessibilityEvent
import io.github.openflocon.flocon.accessibility.model.AccessibilityItem
import io.github.openflocon.flocon.accessibility.model.AccessibilityStateItem
import io.github.openflocon.flocon.accessibility.model.toItem
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.encode
import io.github.openflocon.flocon.error.pluginNotInitialized

class FloconAccessibilityConfig internal constructor(
    internal val context: FloconContext,
) : FloconPluginConfig

interface FloconAccessibilityPlugin : FloconPlugin {
    fun log(event: AccessibilityEvent)
    fun log(events: List<AccessibilityEvent>)

    /** Logs an `ANNOUNCEMENT` event, for the text your app announces to a screen reader. */
    fun announce(text: String)
}

object FloconAccessibility : FloconPluginFactory<FloconAccessibilityConfig, FloconAccessibilityPlugin> {
    override val name: String = "Accessibility"
    override val pluginId: String = Protocol.ToDevice.Accessibility.Plugin
    override fun createConfig(context: FloconContext) = FloconAccessibilityConfig(context)
    override fun install(
        pluginConfig: FloconAccessibilityConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconAccessibilityPlugin {
        return FloconAccessibilityPluginImpl(
            sender = floconConfig.client as FloconMessageSender,
            encoder = encoder
        )
            .also {
                FloconAccessibilityPluginImpl.plugin = it
                it.startCapture(pluginConfig.context)
            }
    }
}

internal class FloconAccessibilityPluginImpl(
    private val sender: FloconMessageSender,
    private val encoder: FloconEncoder
) : FloconPlugin, FloconAccessibilityPlugin {
    override val key: String
        get() = Protocol.ToDevice.Accessibility.Plugin

    private var lastState: AccessibilityStateItem = unsupportedState

    fun startCapture(context: FloconContext) {
        try {
            startAccessibilityCapture(
                context = context,
                onEvent = { log(it) },
                onState = ::sendState,
            )
        } catch (t: Throwable) {
            FloconLogger.logError("error on startAccessibilityCapture", t)
        }
    }

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        // no op
    }

    override suspend fun onConnectedToServer() {
        sendState(lastState)
    }

    override fun log(event: AccessibilityEvent) {
        log(listOf(event))
    }

    override fun log(events: List<AccessibilityEvent>) {
        events.takeIf { it.isNotEmpty() }
            ?.map(AccessibilityEvent::toItem)
            ?.let { toSend ->
                try {
                    sender.send(
                        plugin = Protocol.FromDevice.Accessibility.Plugin,
                        method = Protocol.FromDevice.Accessibility.Method.AddEvents,
                        body = encoder.encode<List<AccessibilityItem>>(toSend)
                    )
                } catch (t: Throwable) {
                    FloconLogger.logError("error on sendAccessibilityEvents", t)
                }
            }
    }

    override fun announce(text: String) {
        log(AccessibilityEvent(type = ANNOUNCEMENT_TYPE, text = text))
    }

    internal fun sendState(state: AccessibilityStateItem) {
        lastState = state
        try {
            sender.send(
                plugin = Protocol.FromDevice.Accessibility.Plugin,
                method = Protocol.FromDevice.Accessibility.Method.State,
                body = encoder.encode<AccessibilityStateItem>(state)
            )
        } catch (t: Throwable) {
            FloconLogger.logError("error on sendAccessibilityState", t)
        }
    }

    companion object {
        var plugin: FloconAccessibilityPlugin? = null

        internal const val ANNOUNCEMENT_TYPE = "ANNOUNCEMENT"

        private val unsupportedState = AccessibilityStateItem(
            supported = false,
            enabled = false,
            touchExploration = false,
            services = emptyList(),
        )
    }
}

val Flocon.Companion.accessibilityPlugin: FloconAccessibilityPlugin
    get() = FloconAccessibilityPluginImpl.plugin ?: pluginNotInitialized("Accessibility", NoOpAccessibilityPlugin())

private class NoOpAccessibilityPlugin : FloconAccessibilityPlugin {
    override val key: String = ""
    override fun log(event: AccessibilityEvent) = Unit
    override fun log(events: List<AccessibilityEvent>) = Unit
    override fun announce(text: String) = Unit
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}
