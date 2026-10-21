package io.github.openflocon.flocon.featureflags

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.featureflags.model.FloconFeatureFlag

class FloconFeatureFlagsConfig : FloconPluginConfig

interface FloconFeatureFlagsPlugin : FloconPlugin {
    fun register(provider: String, flags: List<FloconFeatureFlag>)
    fun register(provider: String, vararg flags: FloconFeatureFlag)
}

object FloconFeatureFlags : FloconPluginFactory<FloconFeatureFlagsConfig, FloconFeatureFlagsPlugin> {
    override val name: String = "FeatureFlags"
    override val pluginId: String = "featureflags"
    override fun createConfig(context: FloconContext) = FloconFeatureFlagsConfig()
    override fun install(
        pluginConfig: FloconFeatureFlagsConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconFeatureFlagsPlugin = FloconFeatureFlagsPluginNoOp
}

internal object FloconFeatureFlagsPluginNoOp : FloconFeatureFlagsPlugin {
    override val key: String = "featureflags"
    override fun register(provider: String, flags: List<FloconFeatureFlag>) = Unit
    override fun register(provider: String, vararg flags: FloconFeatureFlag) = Unit
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}

val Flocon.Companion.featureFlagsPlugin: FloconFeatureFlagsPlugin
    get() = FloconFeatureFlagsPluginNoOp
