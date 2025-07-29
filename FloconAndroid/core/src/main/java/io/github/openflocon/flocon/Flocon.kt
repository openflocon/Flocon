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
import kotlinx.coroutines.launch

object Flocon {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    interface Client : FloconMessageSender {

        @Throws(Throwable::class)
        suspend fun connect(onClosed: () -> Unit)
        fun disconnect()

        val dashboardPlugin: FloconDashboardPlugin
        val tablePlugin: FloconTablePlugin
        val deeplinksPlugin: FloconDeeplinksPlugin
        val analyticsPlugin: FloconAnalyticsPlugin
    }

    var client: Flocon.Client? = null
        private set

    fun initialize(context: Context) {
        val app = context.applicationContext
        val newClient = FloconClientImpl(app)
        client = newClient

        scope.launch {
            start(newClient)
        }
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
        } catch (t: Throwable) {
            //t.printStackTrace()
            delay(3_000)
            start(client)
        }
    }

}