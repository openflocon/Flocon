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
import io.github.openflocon.flocon.accessibility.model.AccessibilityFocusedNodeItem
import io.github.openflocon.flocon.accessibility.model.AccessibilityItem
import io.github.openflocon.flocon.accessibility.model.AccessibilityStateItem
import io.github.openflocon.flocon.accessibility.model.toFocusedNodeItem
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

    /** Bounds of the element that last got the accessibility focus, null when it was cleared. */
    private var focusedBounds: String? = null

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
        val request = when (method) {
            Protocol.ToDevice.Accessibility.Method.GetFocusedNode -> AccessibilityFocusRequest.Current
            Protocol.ToDevice.Accessibility.Method.FocusNext -> AccessibilityFocusRequest.Next
            Protocol.ToDevice.Accessibility.Method.FocusPrevious -> AccessibilityFocusRequest.Previous
            else -> return
        }
        try {
            val node = requestAccessibilityFocus(request, focusedBounds)
            if (request != AccessibilityFocusRequest.Current) {
                // the focus event comes later, don't wait for it to know where we are on a quick succession of requests
                focusedBounds = node.bounds ?: focusedBounds
            }
            sendFocusedNode(node)
        } catch (t: Throwable) {
            FloconLogger.logError("error on accessibility focus request", t)
        }
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
        events.forEach(::trackFocus)
    }

    override fun announce(text: String) {
        log(AccessibilityEvent(type = ANNOUNCEMENT_TYPE, text = text))
    }

    private fun trackFocus(event: AccessibilityEvent) {
        when (event.type) {
            ACCESSIBILITY_FOCUSED_TYPE -> {
                focusedBounds = event.bounds
                sendFocusedNode(event.toFocusedNodeItem())
            }
            ACCESSIBILITY_FOCUS_CLEARED_TYPE -> {
                focusedBounds = null
                sendFocusedNode(AccessibilityFocusedNodeItem())
            }
        }
    }

    private fun sendFocusedNode(node: AccessibilityFocusedNodeItem) {
        try {
            sender.send(
                plugin = Protocol.FromDevice.Accessibility.Plugin,
                method = Protocol.FromDevice.Accessibility.Method.FocusedNode,
                body = encoder.encode<AccessibilityFocusedNodeItem>(node)
            )
        } catch (t: Throwable) {
            FloconLogger.logError("error on sendAccessibilityFocusedNode", t)
        }
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
        internal const val ACCESSIBILITY_FOCUSED_TYPE = "VIEW_ACCESSIBILITY_FOCUSED"
        internal const val ACCESSIBILITY_FOCUS_CLEARED_TYPE = "VIEW_ACCESSIBILITY_FOCUS_CLEARED"

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
