@file:OptIn(FloconInternalApi::class)

package io.github.openflocon.flocon.client

import io.github.openflocon.flocon.FloconAddressStore
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconConnectResult
import io.github.openflocon.flocon.FloconConnectionState
import io.github.openflocon.flocon.FloconInternalApi
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.model.toBonjourBody
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * The connections to the desktop app: the default address, always tried, and one more address
 * (given at start, saved from the last launch, or given with `Flocon.connect`).
 *
 * Each connection is tried again every 3 seconds until it succeeds, and when it is lost.
 */
internal class FloconConnections(
    private val config: FloconConfig,
    private val plugins: List<FloconPlugin>,
    private val encoder: FloconEncoder,
    private val initialAddress: String?,
    private val addressStore: FloconAddressStore?,
    private val onMessageReceived: (String) -> Unit,
) {

    private val connectMutex = Mutex()

    private val client get() = config.client

    /** Starts the connection to the default address, then to the address of the previous launch. */
    fun start() {
        // the default address (localhost on an emulator) is always tried, whatever the other addresses
        launch(client.defaultLink)

        // then the address given by the app, or the last one that worked: the latter wins
        val address = addressStore?.load() ?: initialAddress ?: return
        try {
            val server = parseFloconServer(address)
            if (server != client.defaultLink.server) {
                launch(client.addExtraLink(server))
            }
        } catch (e: IllegalArgumentException) {
            FloconLogger.logError("Ignoring the address '$address': ${e.message}", e)
        }
    }

    /** Adds a connection to [address], next to the default one, replacing the one added before. */
    suspend fun connectTo(address: String): FloconConnectResult = connectMutex.withLock {
        val server = try {
            parseFloconServer(address)
        } catch (e: IllegalArgumentException) {
            return FloconConnectResult.InvalidAddress(e.message ?: address)
        }
        val defaultLink = client.defaultLink
        if (server == defaultLink.server) {
            return if (defaultLink.isConnected) {
                FloconConnectResult.Connected
            } else {
                FloconConnectResult.Failed("$address is the default address, Flocon is already trying to connect to it")
            }
        }
        client.extraLink?.let { removeExtraLink(it) }

        val link = client.addExtraLink(server)
        try {
            connectOnce(link)
            FloconConnectResult.Connected
        } catch (cancellation: CancellationException) {
            removeExtraLink(link)
            throw cancellation
        } catch (t: Throwable) {
            // the messages stay kept for the other addresses, call connect again to retry
            removeExtraLink(link)
            FloconConnectResult.Failed(t.message ?: t.toString())
        }
    }

    private fun launch(link: FloconLink) {
        FloconConnectionStates.set(link, FloconConnectionState.Connecting)
        link.job = config.scope.launch { connectUntilSuccess(link) }
    }

    /** Connects to the desktop app, trying again every 3 seconds until it succeeds. */
    private suspend fun connectUntilSuccess(link: FloconLink) {
        while (true) {
            try {
                connectOnce(link)
                return
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (t: Throwable) {
                FloconConnectionStates.set(link, FloconConnectionState.Failed(t.message ?: t.toString()))
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
        FloconConnectionStates.set(link, FloconConnectionState.Connecting)
        client.connect(
            link = link,
            onClosed = {
                FloconLogger.log("Client - Closed ${link.server}")
                // the messages are kept until the next connection, try again to connect
                FloconConnectionStates.set(link, FloconConnectionState.Buffering)
                launch(link)
            },
            onMessageReceived = onMessageReceived,
        )
        // before the plugins: what they send on connection comes after the handshake
        client.onConnected(link, handshakeBody = plugins.toBonjourBody(encoder))
        FloconConnectionStates.set(link, FloconConnectionState.Connected)
        // the address typed by the user, or given at start, comes back at the next launch
        if (link !== client.defaultLink) addressStore?.save(link.server.host)
        plugins.forEach { it.onConnectedToServer() }
    }

    private suspend fun removeExtraLink(link: FloconLink) {
        // stop the retries to the previous address
        link.job?.cancelAndJoin()
        link.job = null
        client.removeExtraLink(link)
        FloconConnectionStates.clear(link)
    }
}
