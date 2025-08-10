package io.github.openflocon.flocon

import android.content.Context
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.plugins.analytics.FloconAnalyticsPlugin
import io.github.openflocon.flocon.plugins.dashboard.FloconDashboardPlugin
import io.github.openflocon.flocon.plugins.deeplinks.FloconDeeplinksPlugin
import io.github.openflocon.flocon.plugins.tables.FloconTablePlugin

interface FloconApp {

    interface Client : FloconMessageSender {

        @Throws(Throwable::class)
        suspend fun connect(onClosed: () -> Unit)
        fun disconnect()

        val dashboardPlugin: FloconDashboardPlugin
        val tablePlugin: FloconTablePlugin
        val deeplinksPlugin: FloconDeeplinksPlugin
        val analyticsPlugin: FloconAnalyticsPlugin
    }

    val client: FloconApp.Client?

    fun initialize(context: Context)

}