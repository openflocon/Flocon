@file:OptIn(FloconMarker::class)

package io.github.openflocon.flocon

import io.github.openflocon.flocon.FloconApp.Client
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.decode
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.model.FloconMessageFromServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Flocon internal constructor(
    private val config: FloconConfig,
    private val plugins: List<FloconPlugin>,
    private val encoder: FloconEncoder
) {

    init {
        config.scope.launch {
            start(
                client = config.client,
                context = config.context,
            )
        }
    }

    private suspend fun start(client: Client, context: FloconContext) {
        // try to connect, it fail : try again in 3s
        try {
            client.connect(
                onClosed = {
                    println("Client - Closed")
                    // try again to connect
                    config.scope.launch {
                        start(
                            client = client,
                            context = context,
                        )
                    }
                },
                onMessageReceived = ::onMessageReceived
            )

            plugins.forEach { it.onConnectedToServer() }

            (client as? FloconMessageSender)?.let {
                // if success, just send a bonjour
                it.send("bonjour", method = "bonjour", body = "bonjour")
                it.sendPendingMessages()
            }
        } catch (t: Throwable) {
            if (t.message?.contains("CLEARTEXT communication to localhost not permitted by network security policy") == true) {
                withContext(Dispatchers.Main) {
                    displayClearTextError(context = context)
                }
            } else {
                t.printStackTrace()
                delay(3_000)
                start(
                    client = client,
                    context = context,
                )
            }
        }
    }

    private fun onMessageReceived(message: String) {
        println("Message received : $message")
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
    }

}