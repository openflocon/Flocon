package io.github.openflocon.flocon.network.core.noop

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.error.pluginNotInitialized
import io.github.openflocon.flocon.network.core.noop.plugin.FloconNetworkPluginImpl
import io.github.openflocon.flocon.network.core.FloconNetworkConfig
import io.github.openflocon.flocon.network.core.FloconNetworkPlugin

object FloconNetwork : FloconPluginFactory<FloconNetworkConfig, FloconNetworkPlugin> {
    override val name: String = "Network"
    override val pluginId: String = Protocol.ToDevice.Network.Plugin

    override fun createConfig() = FloconNetworkConfig()

    @OptIn(FloconMarker::class)
    override fun install(
        pluginConfig: FloconNetworkConfig,
        floconConfig: FloconConfig
    ): FloconNetworkPlugin {
        return FloconNetworkPluginImpl()
            .also { FloconNetworkPluginImpl.plugin = it }
    }

}

@OptIn(FloconMarker::class)
val Flocon.Companion.networkPlugin: FloconNetworkPlugin
    get() = FloconNetworkPluginImpl.plugin ?: pluginNotInitialized("Network")