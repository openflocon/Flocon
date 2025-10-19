package io.github.openflocon.flocon

import android.content.Context
import io.github.openflocon.flocon.client.FloconClientImpl
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.plugins.analytics.FloconAnalyticsPlugin
import io.github.openflocon.flocon.plugins.dashboard.FloconDashboardPlugin
import io.github.openflocon.flocon.plugins.deeplinks.FloconDeeplinksPlugin
import io.github.openflocon.flocon.plugins.tables.FloconTablePlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Flocon : FloconApp() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var _client: FloconApp.Client? = null

    override val client: FloconApp.Client?
        get() {
            return _client
        }

    override val isInitialized = MutableStateFlow(false)

    override fun initialize(context: Context) {
        val app = context.applicationContext
        val newClient = FloconClientImpl(app)
        _client = newClient
        isInitialized.value = true

        scope.launch {
            start(newClient)
        }

        super.initialize(context)
    }

    private suspend fun start(client: FloconClientImpl) {
        // try to connect, it fail : try again in 3s
        try {
            client.connect(
                onClosed = {
                    // try again to connect
                    scope.launch {
                        start(client)
                    }
                }
            )
            // if success, just send a bonjour
            client.send("bonjour", method = "bonjour", body = "bonjour")
            client.sendPendingMessages()
        } catch (t: Throwable) {
            if(t.message?.contains("CLEARTEXT communication to localhost not permitted by network security policy") == true) {
                withContext(Dispatchers.Main) {
                    client.displayClearTextError()
                }
            } else {
                //t.printStackTrace()
                delay(3_000)
                start(client)
            }
        }
    }

}