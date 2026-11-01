package io.github.openflocon.flocon.accessibility

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.accessibility.model.AccessibilityEvent
import io.github.openflocon.flocon.core.FloconEncoder

class FloconAccessibilityConfig : FloconPluginConfig

interface FloconAccessibilityPlugin : FloconPlugin {
    fun log(event: AccessibilityEvent)
    fun log(events: List<AccessibilityEvent>)
    fun announce(text: String)
}

object FloconAccessibility : FloconPluginFactory<FloconAccessibilityConfig, FloconAccessibilityPlugin> {
    override val name: String = "Accessibility"
    override val pluginId: String = "accessibility"
    override fun createConfig(context: FloconContext) = FloconAccessibilityConfig()
    override fun install(
        pluginConfig: FloconAccessibilityConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconAccessibilityPlugin = FloconAccessibilityPluginNoOp
}

internal object FloconAccessibilityPluginNoOp : FloconAccessibilityPlugin {
    override val key: String = "accessibility"
    override fun log(event: AccessibilityEvent) = Unit
    override fun log(events: List<AccessibilityEvent>) = Unit
    override fun announce(text: String) = Unit
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}

val Flocon.Companion.accessibilityPlugin: FloconAccessibilityPlugin
    get() = FloconAccessibilityPluginNoOp
