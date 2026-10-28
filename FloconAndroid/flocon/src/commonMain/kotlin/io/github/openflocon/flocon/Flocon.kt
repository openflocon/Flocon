package io.github.openflocon.flocon

import io.github.openflocon.flocon.client.FloconLink
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
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class Flocon internal constructor(
    private val config: FloconConfig,
    private val plugins: List<FloconPlugin>,
    private val encoder: FloconEncoder,
) {

    private val connectMutex = Mutex()

    init {
        // the default address (localhost on an emulator) is always tried, whatever the other addresses
        val link = config.client.defaultLink
        setLinkState(link, FloconConnectionState.Connecting)
        link.job = launchConnection(link)
    }

    private fun launchConnection(link: FloconLink): Job = config.scope.launch {
        start(link = link, context = config.context)
    }

    /** Connects to the desktop app, trying again every 3 seconds until it succeeds. */
    private suspend fun start(link: FloconLink, context: FloconContext) {
        while (true) {
            try {
                connectOnce(link)
                return
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (t: Throwable) {
                setLinkState(link, FloconConnectionState.Failed(t.message ?: t.toString()))
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
    private suspend fun connectOnce(link: FloconLink) {
        setLinkState(link, FloconConnectionState.Connecting)
        config.client.connect(
            link = link,
            onClosed = {
                FloconLogger.log("Client - Closed ${link.server}")
                // the messages are kept until the next connection, try again to connect
                setLinkState(link, FloconConnectionState.Buffering)
                link.job = launchConnection(link)
            },
            onMessageReceived = ::onMessageReceived
        )
        // before the plugins: what they send on connection comes after the handshake
        config.client.onConnected(link, handshakeBody = plugins.toBonjourBody(encoder))
        setLinkState(link, FloconConnectionState.Connected)
        plugins.forEach { it.onConnectedToServer() }
    }

    /** Adds a connection to [url], next to the default one, replacing the one added before. */
    private suspend fun connectTo(url: String): FloconConnectResult = connectMutex.withLock {
        val server = try {
            parseFloconServer(url)
        } catch (e: IllegalArgumentException) {
            return FloconConnectResult.InvalidAddress(e.message ?: url)
        }
        val defaultLink = config.client.defaultLink
        if (server == defaultLink.server) {
            return if (defaultLink.isConnected) {
                FloconConnectResult.Connected
            } else {
                FloconConnectResult.Failed("$url is the default address, Flocon is already trying to connect to it")
            }
        }
        config.client.extraLink?.let { removeExtraLink(it) }

        val link = config.client.addExtraLink(server)
        try {
            connectOnce(link)
            FloconConnectResult.Connected
        } catch (cancellation: CancellationException) {
            removeExtraLink(link)
            throw cancellation
        } catch (t: Throwable) {
            // the messages stay kept for the other addresses, call connect again to retry
            val reason = t.message ?: t.toString()
            removeExtraLink(link)
            FloconConnectResult.Failed(reason)
        }
    }

    private suspend fun removeExtraLink(link: FloconLink) {
        // stop the retries to the previous address
        link.job?.cancelAndJoin()
        link.job = null
        config.client.removeExtraLink(link)
        clearLinkState(link)
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
        private val linkStates = MutableStateFlow<Map<FloconLink, FloconConnectionState>>(emptyMap())

        private fun setLinkState(link: FloconLink, state: FloconConnectionState) {
            publish(linkStates.updateAndGet { it + (link to state) })
        }

        private fun clearLinkState(link: FloconLink) {
            publish(linkStates.updateAndGet { it - link })
        }

        /** Connected as soon as one address is, else the most advanced state of the addresses. */
        private fun publish(states: Map<FloconLink, FloconConnectionState>) {
            val all = states.values
            mutableConnectionState.value = all.firstOrNull { it is FloconConnectionState.Connected }
                ?: all.firstOrNull { it is FloconConnectionState.Connecting }
                ?: all.firstOrNull { it is FloconConnectionState.Failed }
                ?: all.firstOrNull()
                ?: FloconConnectionState.Idle
        }

        /** The state of the connection to the desktop app. */
        val connectionState: StateFlow<FloconConnectionState> get() = mutableConnectionState

        /**
         * Connects to the desktop app at the given address, in addition to the default one.
         *
         * Flocon always keeps trying the default address (localhost on an emulator), so this is
         * meant for what it cannot guess, such as the IP of the computer from a real device, typically
         * typed in a debug menu. Opens the connection, introduces the app to the desktop, then sends
         * everything that was logged and not sent yet, in the order it was logged, and sends the next
         * messages right away. If the connection is lost later, the messages are kept again and sent
         * on reconnection, which is retried every 3 seconds. Calling it again replaces the address.
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
