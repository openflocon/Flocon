package io.github.openflocon.flocon

import io.github.openflocon.flocon.client.FloconConnectionStates
import io.github.openflocon.flocon.client.FloconConnections
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.decode
import io.github.openflocon.flocon.model.FloconMessageFromServer
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class Flocon internal constructor(
    private val config: FloconConfig,
    private val plugins: List<FloconPlugin>,
    private val encoder: FloconEncoder,
    initialAddress: String? = null,
    addressStore: FloconAddressStore? = null,
) {

    private val connections = FloconConnections(
        config = config,
        plugins = plugins,
        encoder = encoder,
        initialAddress = initialAddress,
        addressStore = addressStore,
        onMessageReceived = ::onMessageReceived,
    )

    init {
        connections.start()
    }

    private fun onMessageReceived(message: String) {
        FloconLogger.log("Message received : $message")
        config.scope.launch {
            try {
                val serialized = encoder.decode<FloconMessageFromServer>(message)
                    ?: return@launch

                plugins.find { it.key == serialized.plugin }
                    ?.onMessageReceived(
                        method = serialized.method,
                        body = serialized.body
                    )
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }
    }

    companion object {
        var instance: Flocon? = null
            get() = field ?: error("Flocon is not initialized")
            internal set(value) {
                field = value
                started = value
            }

        // `instance` throws when Flocon is not started
        private var started: Flocon? = null

        /** The state of the connection to the desktop app. */
        internal val connectionStateFlow: StateFlow<FloconConnectionState> get() = FloconConnectionStates.state

        /**
         * Connects to the desktop app at the given address, in addition to the default one,
         * see `Flocon.connect`.
         */
        internal suspend fun connectToServer(address: String): FloconConnectResult =
            started?.connections?.connectTo(address) ?: FloconConnectResult.NotStarted
    }

}
