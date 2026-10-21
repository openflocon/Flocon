package io.github.openflocon.flocon.plugins.featureflags

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.featureflags.mapper.featureFlagsToJson
import io.github.openflocon.flocon.plugins.featureflags.model.FloconFeatureFlag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal class FloconFeatureFlagsPluginImpl(
    private val sender: FloconMessageSender,
) : FloconPlugin, FloconFeatureFlagsPlugin {

    private val featureFlagsDispatcher = Dispatchers.Default.limitedParallelism(1)

    private val scope = CoroutineScope(featureFlagsDispatcher + SupervisorJob())

    // provider -> flags
    private val featureFlags = mutableMapOf<String, List<FloconFeatureFlag>>()

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
    ) {
        // no op, the desktop is read only for now
    }

    override fun onConnectedToServer() {
        // on connected, send known feature flags
        scope.launch {
            featureFlags.forEach { (provider, flags) ->
                send(provider = provider, flags = flags)
            }
        }
    }

    override fun registerFeatureFlags(provider: String, flags: List<FloconFeatureFlag>) {
        scope.launch {
            featureFlags[provider] = flags
            send(provider = provider, flags = flags)
        }
    }

    private fun send(provider: String, flags: List<FloconFeatureFlag>) {
        try {
            sender.send(
                plugin = Protocol.FromDevice.FeatureFlags.Plugin,
                method = Protocol.FromDevice.FeatureFlags.Method.Register,
                body = featureFlagsToJson(provider = provider, flags = flags),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("feature flags error", t)
        }
    }
}
