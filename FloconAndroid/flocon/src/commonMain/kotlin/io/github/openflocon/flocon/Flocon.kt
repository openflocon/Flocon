package io.github.openflocon.flocon

import io.github.openflocon.flocon.FloconApp.Client
import io.github.openflocon.flocon.client.FloconClientImpl
import io.github.openflocon.flocon.core.FloconMessageSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class Flocon(
    private val context: FloconContext,
    private val plugins: List<FloconPlugin>
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val client = FloconClientImpl(
        context = context,
        configuration = FloconConfiguration(),
        plugins = plugins
    )

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
                }
            )
            (client as? FloconMessageSender)?.let {
                // if success, just send a bonjour
                it.send("bonjour", method = "bonjour", body = "bonjour")
                it.sendPendingMessages()
            }
        } catch (t: Throwable) {
            if(t.message?.contains("CLEARTEXT communication to localhost not permitted by network security policy") == true) {
                withContext(Dispatchers.Main) {
                    displayClearTextError(context = context)
                }
            } else {
                //t.printStackTrace()
                delay(3_000)
                start(
                    client = client,
                    context = context,
                )
            }
        }
    }

}