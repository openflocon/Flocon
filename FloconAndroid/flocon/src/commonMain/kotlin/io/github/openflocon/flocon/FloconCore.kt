package io.github.openflocon.flocon

import io.github.openflocon.flocon.client.FloconClientImpl
import io.github.openflocon.flocon.core.FloconMessageSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

expect class FloconContext

abstract class FloconCore : FloconApp() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var _client: Client? = null

    override val client: Client?
        get() {
            return _client
        }

    private val _isInitialized = MutableStateFlow(false)
    override val isInitialized: StateFlow<Boolean> = _isInitialized

    protected fun initializeFlocon(context: FloconContext) {
        val newClient = FloconClientImpl(context)
        _client = newClient
        
        // Setup crash handler early to catch crashes during initialization
        newClient.crashReporterPlugin.setupCrashHandler()
        
        _isInitialized.value = true

        scope.launch {
            start(
                client = newClient,
                context = context
            )
        }

        super.initializeFlocon()
    }

    private suspend fun start(client: FloconApp.Client, context: FloconContext) {
        // try to connect, it fail : try again in 3s
        try {
            client.connect(
                onClosed = {
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

internal expect fun displayClearTextError(context: FloconContext)