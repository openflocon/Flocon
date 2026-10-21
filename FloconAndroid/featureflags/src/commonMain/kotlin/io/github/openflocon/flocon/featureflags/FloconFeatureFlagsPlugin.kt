package io.github.openflocon.flocon.featureflags

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
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.error.pluginNotInitialized
import io.github.openflocon.flocon.featureflags.mapper.toSerializable
import io.github.openflocon.flocon.featureflags.mapper.FeatureFlagsSerializable
import io.github.openflocon.flocon.featureflags.model.FloconFeatureFlag
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FloconFeatureFlagsConfig : FloconPluginConfig

interface FloconFeatureFlagsPlugin : FloconPlugin {
    /**
     * Replaces all the flags known for [provider] with [flags]
     */
    fun register(provider: String, flags: List<FloconFeatureFlag>)

    /**
     * Replaces all the flags known for [provider] with [flags]
     */
    fun register(provider: String, vararg flags: FloconFeatureFlag)
}

object FloconFeatureFlags : FloconPluginFactory<FloconFeatureFlagsConfig, FloconFeatureFlagsPlugin> {
    override val name: String = "FeatureFlags"
    override val pluginId: String = Protocol.FromDevice.FeatureFlags.Plugin
    override fun createConfig(context: FloconContext) = FloconFeatureFlagsConfig()
    override fun install(
        pluginConfig: FloconFeatureFlagsConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconFeatureFlagsPlugin {
        return FloconFeatureFlagsPluginImpl(
            sender = floconConfig.client as FloconMessageSender,
            encoder = encoder,
        )
            .also { FloconFeatureFlagsPluginImpl.plugin = it }
    }
}

internal class FloconFeatureFlagsPluginImpl(
    private val sender: FloconMessageSender,
    private val encoder: FloconEncoder,
    dispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
) : FloconPlugin, FloconFeatureFlagsPlugin {

    // the desktop is read only for now: there is no message to receive
    override val key: String = Protocol.FromDevice.FeatureFlags.Plugin

    // one coroutine at a time: the flags of the providers are only used from it
    private val scope = CoroutineScope(dispatcher + SupervisorJob())

    // provider -> flags, sent again when the app reconnects to the desktop
    private val featureFlags = mutableMapOf<String, List<FloconFeatureFlag>>()

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        // no op
    }

    override suspend fun onConnectedToServer() {
        // on connected, send the known feature flags
        scope.launch {
            featureFlags.forEach { (provider, flags) ->
                send(provider = provider, flags = flags)
            }
        }
    }

    override fun register(provider: String, flags: List<FloconFeatureFlag>) {
        scope.launch {
            featureFlags[provider] = flags
            send(provider = provider, flags = flags)
        }
    }

    override fun register(provider: String, vararg flags: FloconFeatureFlag) {
        register(provider = provider, flags = flags.toList())
    }

    private fun send(provider: String, flags: List<FloconFeatureFlag>) {
        try {
            sender.send(
                plugin = Protocol.FromDevice.FeatureFlags.Plugin,
                method = Protocol.FromDevice.FeatureFlags.Method.Register,
                body = encoder.encode(
                    FeatureFlagsSerializable(
                        provider = provider,
                        flags = flags.map { it.toSerializable() },
                    )
                ),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("feature flags error", t)
        }
    }

    companion object {
        var plugin: FloconFeatureFlagsPlugin? = null
    }
}

@OptIn(FloconMarker::class)
val Flocon.Companion.featureFlagsPlugin: FloconFeatureFlagsPlugin
    get() = FloconFeatureFlagsPluginImpl.plugin ?: pluginNotInitialized("FeatureFlags", NoOpFeatureFlagsPlugin())

private class NoOpFeatureFlagsPlugin : FloconFeatureFlagsPlugin {
    override val key: String = ""
    override fun register(provider: String, flags: List<FloconFeatureFlag>) = Unit
    override fun register(provider: String, vararg flags: FloconFeatureFlag) = Unit
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}
