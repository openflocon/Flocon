package io.github.openflocon.flocon

import io.github.openflocon.flocon.FloconApp.Client
import io.github.openflocon.flocon.client.FloconClient
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.model.floconMessageFromServerFromJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class Flocon(
    private val context: FloconContext,
    private val scope: CoroutineScope,
    private val client: FloconClient,
    private val plugins: List<FloconPlugin>
) {

    init {
        scope.launch {
            start(
                client = client,
                context = context,
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
                    scope.launch {
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
        scope.launch(Dispatchers.IO) {
            floconMessageFromServerFromJson(message)?.let { messageFromServer ->
                plugins.find { it.key == messageFromServer.plugin }
                    ?.onMessageReceived(
                        method = messageFromServer.method,
                        body = messageFromServer.body,
                    )
            }
        }
    }

}