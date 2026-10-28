package io.github.openflocon.flocon

import io.github.openflocon.flocon.client.FloconClient
import io.github.openflocon.flocon.client.parseFloconServer
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.decode
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.model.toBonjourBody
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Flocon internal constructor(
    private val config: FloconConfig,
    private val plugins: List<FloconPlugin>,
    private val encoder: FloconEncoder,
    connectsAutomatically: Boolean = true,
) {

    private var connectJob: Job? = null

    init {
        if (connectsAutomatically) {
            connectJob = launchConnection()
        } else {
            // nothing is sent until connect() : what the plugins log is kept in memory
            mutableConnectionState.value = FloconConnectionState.Buffering
        }
    }

    private fun launchConnection(): Job = config.scope.launch {
        start(client = config.client, context = config.context)
    }

    /** Connects to the desktop app, trying again every 3 seconds until it succeeds. */
    private suspend fun start(client: FloconClient, context: FloconContext) {
        while (true) {
            try {
                connectOnce(client)
                return
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (t: Throwable) {
                mutableConnectionState.value = FloconConnectionState.Failed(t.message ?: t.toString())
                if (t.message?.contains("CLEARTEXT communication to localhost not permitted by network security policy") == true) {
                    withContext(Dispatchers.Main) {
                        displayClearTextError(context = context)
                    }
                    return
                }
                t.printStackTrace()
                delay(3_000)
            }
        }
    }

    /**
     * Opens the connection, then sends the handshake with the installed plugins, then everything
     * that was kept since the start, in the order it was logged.
     */
    private suspend fun connectOnce(client: FloconClient) {
        mutableConnectionState.value = FloconConnectionState.Connecting
        client.connect(
            onClosed = {
                FloconLogger.log("Client - Closed")
                // the messages are kept until the next connection, try again to connect
                mutableConnectionState.value = FloconConnectionState.Buffering
                connectJob = launchConnection()
            },
            onMessageReceived = ::onMessageReceived
        )
        // before the plugins: what they send on connection comes after the handshake
        client.onConnected(handshakeBody = plugins.toBonjourBody(encoder))
        mutableConnectionState.value = FloconConnectionState.Connected
        plugins.forEach { it.onConnectedToServer() }
    }

    private suspend fun connectTo(url: String): FloconConnectResult {
        val server = try {
            parseFloconServer(url)
        } catch (e: IllegalArgumentException) {
            return FloconConnectResult.InvalidAddress(e.message ?: url)
        }
        // stop the retries to the previous address
        connectJob?.cancelAndJoin()
        connectJob = null
        config.client.setServer(server)
        config.client.disconnect()
        return try {
            connectOnce(config.client)
            FloconConnectResult.Connected
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (t: Throwable) {
            val reason = t.message ?: t.toString()
            mutableConnectionState.value = FloconConnectionState.Failed(reason)
            FloconConnectResult.Failed(reason)
        }
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
            internal set

        private val mutableConnectionState = MutableStateFlow<FloconConnectionState>(FloconConnectionState.Idle)

        /** The state of the connection to the desktop app. */
        val connectionState: StateFlow<FloconConnectionState> get() = mutableConnectionState

        /**
         * Connects to the desktop app at the given address.
         *
         * Opens the connection, introduces the app to the desktop, then sends everything that was
         * logged since the start, in the order it was logged, and sends the next messages right away.
         * If the connection is lost later, the messages are kept again and sent on reconnection,
         * which is retried every 3 seconds.
         *
         * Use it when Flocon was started with [FloconConfiguration.connectLater], or to point a
         * started Flocon to another address. Call it once at a time.
         *
         * @param url the address of the desktop app, such as `ws://192.168.1.20:9023`,
         * `192.168.1.20:9023` or `192.168.1.20` (port 9023).
         * @return [FloconConnectResult.Connected], or why it could not connect. The messages are kept
         * in every case, call it again to retry.
         */
        suspend fun connect(url: String): FloconConnectResult =
            instance?.connectTo(url) ?: FloconConnectResult.NotStarted
    }

}
