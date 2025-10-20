package io.github.openflocon.flocon

import io.github.openflocon.flocon.plugins.analytics.FloconAnalyticsPlugin
import io.github.openflocon.flocon.plugins.dashboard.FloconDashboardPlugin
import io.github.openflocon.flocon.plugins.deeplinks.FloconDeeplinksPlugin
import io.github.openflocon.flocon.plugins.device.FloconDevicePlugin
import io.github.openflocon.flocon.plugins.network.FloconNetworkPlugin
import io.github.openflocon.flocon.plugins.tables.FloconTablePlugin
import kotlinx.coroutines.flow.StateFlow

abstract class FloconApp {

    companion object {
        var instance: FloconApp? = null
            private set
    }

    interface Client {

        @Throws(Throwable::class)
        suspend fun connect(onClosed: () -> Unit)
        fun disconnect()

        val dashboardPlugin: FloconDashboardPlugin
        val tablePlugin: FloconTablePlugin
        val deeplinksPlugin: FloconDeeplinksPlugin
        val analyticsPlugin: FloconAnalyticsPlugin
        val networkPlugin: FloconNetworkPlugin
        val devicePlugin: FloconDevicePlugin
    }

    open val client: FloconApp.Client? = null

    abstract val isInitialized : StateFlow<Boolean>

    open fun initialize() {
        instance = this
    }

}